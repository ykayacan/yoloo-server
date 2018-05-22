package com.yoloo.server.post.usecase

import com.google.appengine.api.memcache.AsyncMemcacheService
import com.yoloo.server.common.util.AppengineUtil
import com.yoloo.server.api.exception.ServiceExceptions
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.post.entity.Post
import com.yoloo.server.post.entity.Vote
import com.yoloo.server.post.vo.PostPermFlag
import net.cinnom.nanocuckoo.NanoCuckooFilter
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Lazy
@Component
class VotePostUseCase(private val memcacheService: AsyncMemcacheService) {

    fun execute(requesterId: Long, postId: Long) {
        val post = ofy().load().type(Post::class.java).id(postId).now()

        com.yoloo.server.api.exception.ServiceExceptions.checkNotFound(post != null, "post.not_found")
        val votingDisabled = post.flags.contains(PostPermFlag.DISABLE_VOTING)
        com.yoloo.server.api.exception.ServiceExceptions.checkForbidden(!votingDisabled, "post.forbidden_voting")

        val vote = Vote(Vote.createId(requesterId, postId, "p"), 1)

        val voteFilter = memcacheService.get(Vote.KEY_FILTER_VOTE).get() as NanoCuckooFilter
        voteFilter.insert(vote.id)
        val putFuture = memcacheService.put(Vote.KEY_FILTER_VOTE, voteFilter)
        if (AppengineUtil.isTest()) {
            putFuture.get()
        }

        post.countData.voteCount = post.countData.voteCount.inc()
        val result = ofy().save().entities(post, vote)
        if (AppengineUtil.isTest()) {
            result.now()
        }
    }
}