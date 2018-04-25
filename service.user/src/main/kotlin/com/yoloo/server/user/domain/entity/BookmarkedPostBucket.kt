package com.yoloo.server.user.domain.entity

import com.googlecode.objectify.Key
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.yoloo.server.common.mixins.Keyable
import com.yoloo.server.common.mixins.NamespaceKeyProvider
import com.yoloo.server.common.util.NoArg

@NoArg
@Entity
data class BookmarkedPostBucket(@Id var id: String, var ids: List<String>) : Keyable<BookmarkedPostBucket> {

    companion object : NamespaceKeyProvider<BookmarkedPostBucket> {
        override fun createNamespaceId(identifierId: String): String {
            return identifierId
        }

        override fun createNamespaceKey(identifierId: String): Key<BookmarkedPostBucket> {
            return Key.create(BookmarkedPostBucket::class.java, createNamespaceId(identifierId))
        }
    }
}