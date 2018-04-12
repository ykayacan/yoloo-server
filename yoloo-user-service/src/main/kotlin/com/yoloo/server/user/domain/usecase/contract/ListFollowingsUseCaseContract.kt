package com.yoloo.server.user.domain.usecase.contract

import com.yoloo.server.user.domain.response.FollowingResponse
import org.dialectic.jsonapi.response.DataResponse

interface ListFollowingsUseCaseContract {

    data class Request(val userId: String)

    data class Response(val response: DataResponse<FollowingResponse>)
}