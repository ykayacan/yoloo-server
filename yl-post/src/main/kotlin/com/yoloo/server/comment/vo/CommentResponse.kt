package com.yoloo.server.comment.vo

import com.yoloo.server.common.util.NoArg
import com.yoloo.server.post.vo.AuthorResponse
import java.time.LocalDateTime

@NoArg
data class CommentResponse(
    val id: Long,
    val author: AuthorResponse,
    val content: String,
    val approved: Boolean,
    val voted: Boolean,
    val voteCount: Int,
    val createdAt: LocalDateTime
)