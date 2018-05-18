package com.yoloo.server.post.vo

import com.yoloo.server.common.response.attachment.AttachmentResponse
import com.yoloo.server.common.util.NoArg

@NoArg
data class AuthorResponse(
    val id: Long,
    val self: Boolean,
    val displayName: String,
    val image: AttachmentResponse
)