package com.yoloo.server.post.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.yoloo.server.common.util.Fetcher
import com.yoloo.server.common.util.id.LongIdGenerator
import com.yoloo.server.common.vo.AvatarImage
import com.yoloo.server.common.vo.Url
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.post.entity.Post
import com.yoloo.server.post.mapper.PostResponseMapper
import com.yoloo.server.post.vo.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Lazy
@Component
class InsertPostUseCase(
    private val postResponseMapper: PostResponseMapper,
    @Qualifier("cached") private val idGenerator: LongIdGenerator,
    private val userInfoFetcher: Fetcher<Long, UserInfoResponse>,
    private val groupInfoFetcher: Fetcher<Long, GroupInfoResponse>,
    private val pubSubTemplate: PubSubTemplate,
    private val objectMapper: ObjectMapper
) {

    fun execute(requesterId: Long, request: InsertPostRequest): PostResponse {
        val userInfo = userInfoFetcher.fetch(requesterId)
        val groupInfo = groupInfoFetcher.fetch(request.groupId)

        val post = createPost(request, requesterId, userInfo, groupInfo)

        // TODO inc user & group post count
        // TODO If buddy post -> register in buddy search

        ofy().save().entities(post)

        val json = objectMapper.writeValueAsString(post)
        pubSubTemplate.publish("post.create", json, null)

        return postResponseMapper.apply(post, true, false, false)
    }

    private fun createPost(
        request: InsertPostRequest,
        userId: Long,
        userInfo: UserInfoResponse,
        groupInfo: GroupInfoResponse
    ): Post {
        return Post(
            id = idGenerator.generateId(),
            type = findPostType(request),
            author = Author(
                id = userId,
                displayName = userInfo.displayName,
                avatar = AvatarImage(Url(userInfo.image)),
                verified = userInfo.verified
            ),
            content = PostContent(request.content!!),
            title = PostTitle(request.title!!),
            group = PostGroup(groupInfo.id, groupInfo.displayName),
            tags = request.tags!!.map(::PostTag).toSet(),
            coin = if (request.coin == 0) null else PostCoin(request.coin),
            buddyRequest = when (request.buddyInfo) {
                null -> null
                else -> createBuddyRequest(request.buddyInfo)
            }
        )
    }

    private fun findPostType(request: InsertPostRequest): PostType {
        if (request.buddyInfo != null) {
            return PostType.BUDDY
        }
        if (request.attachments != null) {
            return PostType.ATTACHMENT
        }

        return PostType.TEXT
    }

    private fun createBuddyRequest(buddyInfo: BuddyInfo): BuddyRequest {
        return BuddyRequest(
            peopleRange = Range(buddyInfo.fromPeople!!, buddyInfo.toPeople!!),
            location = Location(buddyInfo.location!!),
            dateRange = Range(buddyInfo.fromDate!!, buddyInfo.toDate!!)
        )
    }
}