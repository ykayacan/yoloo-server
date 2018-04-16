package com.yoloo.server.post.application

import com.yoloo.server.common.api.exception.NotFoundException
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.post.domain.entity.Post
import com.yoloo.server.post.domain.response.PostResponse
import com.yoloo.server.post.domain.vo.*
import com.yoloo.server.post.infrastructure.mapper.PostCollectionResponseMapper
import org.dialectic.jsonapi.response.DataResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RequestMapping("/api/v1/posts", produces = ["application/vnd.api+json"], consumes = ["application/vnd.api+json"])
@RestController
class PostControllerV1 @Autowired constructor(val postCollectionResponseMapper: PostCollectionResponseMapper) {

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getPost(@PathVariable("postId") postId: String): DataResponse<PostResponse> {
        val post = ofy().load().type(Post::class.java).id(postId).now()

        if (post == null || post.isDeleted()) {
            throw NotFoundException("post.not-found")
        }

        return postCollectionResponseMapper.apply(Collections.singletonList(post))
    }

    @GetMapping
    fun listPosts(): DataResponse<PostResponse> {
        val list = ofy().load().type(Post::class.java)
            .iterable()
            .asSequence()
            .filter { !it.isDeleted() }
            .toList()

        return postCollectionResponseMapper.apply(list)
    }

    @PostMapping
    fun insertPost() {
        (1..5).map {
            Post(
                author = Author(userId = "userId$it", displayName = "user$it", avatarUrl = ""),
                title = PostTitle("title$it"),
                topic = PostTopic(topicId = "topicId$it", displayName = "topic$it"),
                tags = setOf(PostTag("tag")),
                type = PostType.TEXT,
                content = PostContent.create("lorem impsum")
            )
        }.let { ofy().save().entities(it).now() }
    }
}