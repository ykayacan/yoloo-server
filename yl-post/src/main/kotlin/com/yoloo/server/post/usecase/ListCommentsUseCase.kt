package com.yoloo.server.post.usecase

import com.google.appengine.api.datastore.Cursor
import com.google.appengine.api.datastore.QueryResultIterator
import com.google.appengine.api.memcache.MemcacheService
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.post.entity.Comment
import com.yoloo.server.post.entity.Post
import com.yoloo.server.post.entity.Vote
import com.yoloo.server.post.mapper.CommentResponseMapper
import com.yoloo.server.post.vo.CommentCollectionResponse
import com.yoloo.server.post.vo.CommentResponse
import com.yoloo.server.rest.exception.ServiceExceptions
import net.cinnom.nanocuckoo.NanoCuckooFilter

class ListCommentsUseCase(
    private val commentResponseMapper: CommentResponseMapper,
    private val memcacheService: MemcacheService
) {

    fun execute(requesterId: Long, postId: Long, cursor: String?): CommentCollectionResponse {
        val post = ofy().load().type(Post::class.java).id(postId).now()

        ServiceExceptions.checkNotFound(post != null, "post.not_found")
        ServiceExceptions.checkNotFound(!post.auditData.isDeleted, "post.not_found")

        val queryResultIterator = getQueryResultIterator(postId, cursor)

        if (!queryResultIterator.hasNext()) {
            return CommentCollectionResponse.builder().data(emptyList()).build()
        }

        val voteFilter = memcacheService.get(Vote.KEY_FILTER_VOTE) as NanoCuckooFilter

        val approvedComment = getApprovedCommentResponse(post, requesterId, voteFilter)

        return buildCommentCollectionResponse(
            queryResultIterator,
            requesterId,
            voteFilter,
            approvedComment,
            cursor
        )
    }

    private fun buildCommentCollectionResponse(
        queryResultIterator: QueryResultIterator<Comment>,
        requesterId: Long,
        voteFilter: NanoCuckooFilter,
        approvedComment: CommentResponse?,
        cursor: String?
    ): CommentCollectionResponse {
        return queryResultIterator
            .asSequence()
            .filter { !it.approved }
            .map {
                commentResponseMapper.apply(
                    it,
                    checkIsSelf(requesterId, it),
                    checkIsVoted(voteFilter, requesterId, it)
                )
            }
            .toList()
            .let {
                CommentCollectionResponse.builder()
                    .approvedComment(approvedComment)
                    .data(it)
                    .prevPageToken(cursor)
                    .nextPageToken(queryResultIterator.cursor.toWebSafeString())
                    .build()
            }
    }

    private fun getQueryResultIterator(
        postId: Long,
        cursor: String?
    ): QueryResultIterator<Comment> {
        var query = ofy()
            .load()
            .type(Comment::class.java)
            .filter(Comment.INDEX_POST_ID, postId)
            .orderKey(true)

        cursor?.let { query = query.startAt(Cursor.fromWebSafeString(it)) }
        query = query.limit(50)

        return query.iterator()
    }

    private fun getApprovedCommentResponse(
        post: Post,
        requesterId: Long,
        voteFilter: NanoCuckooFilter
    ): CommentResponse? {
        return post.approvedCommentId?.let {
            val comment = ofy().load().type(Comment::class.java).id(it.value).now()
            return@let commentResponseMapper.apply(
                comment,
                checkIsSelf(requesterId, comment),
                checkIsVoted(voteFilter, requesterId, comment)
            )
        }
    }

    private fun checkIsSelf(requesterId: Long, comment: Comment): Boolean {
        return requesterId == comment.author.id
    }

    private fun checkIsVoted(
        voteFilter: NanoCuckooFilter,
        requesterId: Long,
        comment: Comment
    ): Boolean {
        return voteFilter.contains(Vote.createId(requesterId, comment.id, "p"))
    }
}