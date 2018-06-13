package com.yoloo.server.post.usecase

class InsertCommentUseCaseIntegrationTest {

    /*@get:Rule
    val appEngineRule: AppEngineRule =
        AppEngineRule.builder().withDatastore().withMemcacheService().build()

    private val idGenerator by lazy(LazyThreadSafetyMode.NONE) { CachedSnowflakeIdGenerator() }
    private val commentResponseMapper by lazy(LazyThreadSafetyMode.NONE) { CommentResponseMapper() }
    private val insertCommentUseCase by lazy(LazyThreadSafetyMode.NONE) {
        InsertCommentUseCase(
            idGenerator,
            commentResponseMapper
        )
    }

    @Before
    fun setUp() {
        fact().translators.add(LocalDateTimeDateTranslatorFactory())
        fact().register(Comment::class.java)
        fact().register(Post::class.java)
    }

    @Test
    fun insertComment_postExistsAndValidBody_shouldInsert() {
        createPost()

        val comment = insertCommentUseCase.execute(1, InsertCommentRequest(1, "hello"))

        assertThat(comment.id).isNotNull()
        assertThat(comment.approved).isFalse()

        val userInfo = userInfoFetcher.fetch(1)

        assertThat(comment.author.id).isEqualTo(userInfo.id)
        assertThat(comment.author.self).isEqualTo(userInfo.self)
        assertThat(comment.author.displayName).isEqualTo(userInfo.displayName)
        assertThat(comment.author.verified).isEqualTo(userInfo.verified)
        assertThat(comment.author.image).isEqualTo(
            SimpleAttachmentResponse(
                userInfo.image
            )
        )

        assertThat(comment.content).isNotNull()
        assertThat(comment.createdAt).isNotNull()
        assertThat(comment.voteCount).isEqualTo(0)
        assertThat(comment.voted).isFalse()
    }

    @Test(expected = NotFoundException::class)
    fun insertComment_postNotExistsAndValidBody_shouldThrowNotFound() {
        insertCommentUseCase.execute(1, InsertCommentRequest(1, "hello"))
    }

    private fun createPost() {
        val post = Post(
            id = 1,
            type = PostType.TEXT,
            author = Author(
                id = 2,
                displayName = "stub name",
                avatar = AvatarImage(Url("")),
                verified = false
            ),
            title = PostTitle("lorem impsum title"),
            content = PostContent("lorem impsum content"),
            group = PostGroup(
                id = 3,
                displayName = "group1"
            ),
            tags = setOf(PostTag("tag1"), PostTag("tag2"))
        )

        ofy().save().entity(post).now()
    }*/
}