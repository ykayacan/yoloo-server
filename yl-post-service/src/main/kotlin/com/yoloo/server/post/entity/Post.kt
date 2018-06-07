package com.yoloo.server.post.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.IgnoreSave
import com.googlecode.objectify.condition.IfEmpty
import com.googlecode.objectify.condition.IfNull
import com.yoloo.server.common.entity.BaseEntity
import com.yoloo.server.common.util.NoArg
import com.yoloo.server.post.vo.*
import java.util.*

@NoArg
@Cache(expirationSeconds = Post.CACHE_EXPIRATION_TIME)
@Entity
class Post(
    @JsonProperty("id")
    @Id
    var id: Long,

    @JsonProperty("type")
    var type: PostType,

    var author: Author,

    var content: PostContent,

    var flags: Set<@JvmSuppressWildcards PostPermFlag> = EnumSet.noneOf(PostPermFlag::class.java),

    var title: PostTitle,

    var group: PostGroup,

    var tags: Set<@JvmSuppressWildcards PostTag>,

    @IgnoreSave(IfNull::class)
    var approvedCommentId: ApprovedCommentId? = null,

    var coin: PostCoin? = null,

    @IgnoreSave(IfEmpty::class)
    var attachments: List<@JvmSuppressWildcards PostAttachment> = emptyList(),

    var buddyRequest: BuddyRequest? = null,

    var countData: PostCountData = PostCountData()
) : BaseEntity<Long, Post>() {

    override fun getId(): Long {
        return id
    }

    override fun onLoad() {
        super.onLoad()
        @Suppress("USELESS_ELVIS")
        flags = flags ?: emptySet()
        @Suppress("USELESS_ELVIS")
        attachments = attachments ?: emptyList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        const val INDEX_GROUP_ID = "group.id"
        const val CACHE_EXPIRATION_TIME = 7200
    }
}