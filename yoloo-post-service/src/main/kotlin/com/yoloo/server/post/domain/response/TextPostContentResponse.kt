package com.yoloo.server.post.domain.response

import java.time.LocalDateTime

data class TextPostContentResponse(
    val title: String,
    val content: String,
    val topic: PostTopicResponse,
    val tags: List<String>,
    val approvedCommentId: String?,
    val bounty: Int,
    val count: PostCountResponse,
    val voteDir: Int,
    val createdAt: LocalDateTime
) : PostContentResponse