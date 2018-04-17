package com.yoloo.server.user.domain.usecase.contract

import com.yoloo.server.user.domain.response.UserResponse

interface InsertUserUseCaseContract {

    data class Request(
        val displayName: String,
        val email: String,
        val locale: String,
        val avatarUrl: String,
        val selectedGroupIds: List<String>
    )

    data class Response(val response: UserResponse)
}