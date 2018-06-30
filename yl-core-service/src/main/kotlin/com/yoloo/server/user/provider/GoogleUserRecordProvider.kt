package com.yoloo.server.user.provider

import com.google.firebase.auth.ImportUserRecord
import com.google.firebase.auth.UserImportOptions
import com.google.firebase.auth.UserProvider
import com.yoloo.server.common.id.generator.LongIdGenerator
import com.yoloo.server.user.entity.User
import com.yoloo.server.user.vo.UserCreateRequest

class GoogleUserRecordProvider(private val idGenerator: LongIdGenerator) :
    FirebaseUserRecordProvider {

    override fun provide(request: UserCreateRequest): Pair<ImportUserRecord, UserImportOptions?> {
        val userRecord = ImportUserRecord.builder()
            .setUid(idGenerator.toString())
            .setDisplayName(request.displayName)
            .setEmail(request.email)
            .setPhotoUrl(request.photoUrl)
            .setEmailVerified(false)
            .putCustomClaim("roles", listOf(User.Role.MEMBER))
            .addUserProvider(
                UserProvider.builder()
                    .setUid(request.providerUid)
                    .setEmail(request.email)
                    .setDisplayName(request.displayName)
                    .setPhotoUrl(request.photoUrl)
                    .setProviderId("google.com")
                    .build()
            )
            .build()

        return Pair(userRecord, null)
    }
}