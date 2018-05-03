package com.yoloo.server.post.domain.response.postdata

import com.yoloo.server.post.domain.response.PostCountResponse
import com.yoloo.server.post.domain.response.PostGroupResponse
import java.time.LocalDateTime

open class TextPostDataResponse(
    open var title: String,
    open var group: PostGroupResponse,
    open var tags: List<String>,
    open var approvedCommentId: String?,
    open var bounty: Int,
    open var count: PostCountResponse,
    open var voteDir: Int,
    open var createdAt: LocalDateTime
) : PostDataResponse