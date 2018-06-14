package com.yoloo.server.relationship.usecase

import com.google.appengine.api.memcache.AsyncMemcacheService
import com.google.firebase.messaging.Message
import com.yoloo.server.common.appengine.service.NotificationService
import com.yoloo.server.common.appengine.util.AppengineEnv
import com.yoloo.server.common.exception.exception.ServiceExceptions
import com.yoloo.server.common.exception.exception.ServiceExceptions.checkConflict
import com.yoloo.server.common.util.FcmConstants
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.relationship.entity.Relationship
import com.yoloo.server.user.entity.User
import net.cinnom.nanocuckoo.NanoCuckooFilter

// TODO needs test
class FollowUseCase(
    private val memcacheService: AsyncMemcacheService,
    private val notificationService: NotificationService
) {

    fun execute(requesterId: Long, userId: Long) {
        val map = ofy().load().type(User::class.java).ids(requesterId, userId)
        val fromUser = map[requesterId]
        val toUser = map[userId]

        ServiceExceptions.checkNotFound(toUser != null, "user.not_found")

        val relationshipFilter = getRelationshipFilter()

        checkConflict(
            !Relationship.isFollowing(relationshipFilter, requesterId, userId),
            "relationship.conflict"
        )

        fromUser!!.profile.countData.followingCount = fromUser.profile.countData.followingCount++
        toUser!!.profile.countData.followerCount = fromUser.profile.countData.followerCount++

        val relationship = createRelationship(fromUser, toUser)
        relationshipFilter.insert(relationship.id)

        val saveFuture = ofy().save().entities(fromUser, toUser, relationship)
        val putFuture = memcacheService.put(Relationship.KEY_FILTER_RELATIONSHIP, relationshipFilter)

        sendFollowNotification(toUser.fcmToken, fromUser.profile.displayName.value)

        if (AppengineEnv.isTest()) {
            putFuture.get()
            saveFuture.now()
        }
    }

    private fun getRelationshipFilter(): NanoCuckooFilter {
        return memcacheService.get(Relationship.KEY_FILTER_RELATIONSHIP).get() as NanoCuckooFilter
    }

    private fun createRelationship(fromUser: User, toUser: User): Relationship {
        return Relationship(
            id = Relationship.createId(fromUser.id, toUser.id),
            fromId = fromUser.id,
            toId = toUser.id,
            displayName = fromUser.profile.displayName,
            avatarImage = fromUser.profile.image
        )
    }

    private fun sendFollowNotification(toUserFcmToken: String, followerName: String) {
        val message = Message.builder()
            .setToken(toUserFcmToken)
            .putData(FcmConstants.FCM_KEY_TYPE, FcmConstants.FcmType.FCM_TYPE_FOLLOW.toString())
            .putData("FCM_KEY_FOLLOWER_NAME", followerName)
            .build()

        notificationService.sendAsync(message)
    }
}