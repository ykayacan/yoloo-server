package com.yoloo.server.comment.usecase

import com.googlecode.objectify.Key
import com.yoloo.server.comment.entity.Comment
import com.yoloo.server.comment.vo.ApprovedCommentId
import com.yoloo.server.common.exception.exception.ServiceExceptions
import com.yoloo.server.common.queue.vo.EventType
import com.yoloo.server.common.queue.vo.YolooEvent
import com.yoloo.server.common.queue.service.NotificationQueueService
import com.yoloo.server.common.util.TestUtil
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.post.entity.Post
import com.yoloo.server.user.entity.User

class ApproveCommentUseCase(private val notificationQueueService: NotificationQueueService) {

    fun execute(requesterId: Long, postId: Long, commentId: Long) {
        val postKey = Key.create(Post::class.java, postId)
        val commentKey = Key.create(Comment::class.java, commentId)

        val map = ofy().load().keys(postKey, commentKey) as Map<*, *>

        val post = map[postKey] as Post?
        val comment = map[commentKey] as Comment?

        ServiceExceptions.checkNotFound(comment != null, "comment.not_found")
        ServiceExceptions.checkBadRequest(!comment!!.approved, "comment.conflict_approve")
        ServiceExceptions.checkForbidden(post!!.author.id == requesterId, "post.forbidden_approve")

        val commentUser = ofy().load().type(User::class.java).id(comment.author.id).now()

        comment.approved = true
        post.approvedCommentId = ApprovedCommentId(commentId)

        val saveResult = ofy().save().entities(post, comment)
        TestUtil.saveNow(saveResult)

        addToNotificationQueue(comment, commentUser.fcmToken)
    }

    private fun addToNotificationQueue(comment: Comment, commentAuthorFcmToken: String) {
        val event = YolooEvent.newBuilder(YolooEvent.Metadata.of(EventType.NEW_COMMENT))
            .addData("id", comment.id.toString())
            .addData("postId", comment.postId.value.toString())
            .addData("commentAuthorFcmToken", commentAuthorFcmToken)
            .build()

        notificationQueueService.addQueueAsync(event)
    }
}