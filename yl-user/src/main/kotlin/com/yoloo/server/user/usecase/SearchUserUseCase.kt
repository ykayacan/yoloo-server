package com.yoloo.server.user.usecase

import com.yoloo.server.common.response.CollectionResponse
import com.yoloo.server.user.vo.SearchUserResponse
import org.springframework.stereotype.Component

@Component
class SearchUserUseCase{

    fun execute(query: String, cursor: String?): CollectionResponse<SearchUserResponse> {
        return CollectionResponse.builder<SearchUserResponse>()
            .data(emptyList())
            .prevPageToken(cursor)
            .nextPageToken("")
            .build()
    }
}