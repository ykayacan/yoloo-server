package com.yoloo.server.post.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import com.yoloo.server.common.util.NoArg
import com.yoloo.server.common.vo.Author
import com.yoloo.server.entity.Approvable
import com.yoloo.server.entity.Keyable
import com.yoloo.server.entity.Likeable
import com.yoloo.server.post.vo.CommentContent
import com.yoloo.server.post.vo.PostId
import java.time.Instant

@NoArg
@Entity
data class Comment(
    @Id
    var id: Long,

    @Index
    var postId: PostId,

    var author: Author,

    var content: CommentContent,

    var createdAt: Instant = Instant.now()
) : Keyable<Comment>, Likeable {
    override fun vote() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unvote() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isVotingAllowed(): Boolean {
        return true
    }

    companion object {
        const val INDEX_POST_ID = "postId.value"
        const val INDEX_AUTHOR_ID = "author.id"

        fun createKey(commentId: Long): Key<Comment> {
            return Key.create(Comment::class.java, commentId)
        }
    }
}
