package com.yoloo.server.user.domain.entity

import com.googlecode.objectify.annotation.Cache
import com.googlecode.objectify.annotation.Entity
import com.googlecode.objectify.annotation.Id
import com.googlecode.objectify.annotation.Index
import com.googlecode.objectify.condition.IfTrue
import com.yoloo.server.common.shared.BaseEntity
import com.yoloo.server.common.util.NoArg
import com.yoloo.server.user.domain.vo.*
import java.time.LocalDateTime
import javax.validation.Valid

@Cache
@NoArg
@Entity
data class User constructor(
    @Id var id: Long,

    var displayName: UserDisplayName,

    var url: Url? = null,

    var provider: SocialProvider,

    @field:Valid
    var email: Email,

    var image: AvatarImage,

    var password: Password? = null,

    var gender: Gender,

    @field:Valid
    var lastKnownIP: IP,

    var fcmToken: String,

    var expired: Boolean = false,

    var credentialsExpired: Boolean = false,

    var locked: Boolean = false,

    @Index(IfTrue::class)
    var enabled: Boolean = true,

    var scopes: Set<String>,

    var deletedAt: LocalDateTime? = null,

    var locale: UserLocale,

    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,

    var about: About? = null,

    var website: Url? = null,

    var lastPostTime: LocalDateTime? = null,

    @field:Valid
    var countData: UserCountData = UserCountData(),

    var userFilterData: UserFilterData = UserFilterData(),

    var subscribedGroups: List<UserGroup>,

    // Extra fields for easy mapping
    val self: Boolean = false,

    val following: Boolean = false,

    val followerCount: Long = 0L,

    val followingCount: Long = 0L
) : BaseEntity<Long, User>(1)