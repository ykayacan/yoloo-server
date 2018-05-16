package com.yoloo.server.auth.config

import com.yoloo.server.auth.entity.Account
import com.yoloo.server.auth.entity.Oauth2Client
import com.yoloo.server.objectify.configuration.ObjectifyConfigurer
import org.springframework.stereotype.Component

@Component
class AuthObjectifyConfig : ObjectifyConfigurer {

    override fun registerEntities(): List<Class<*>> {
        return listOf(Account::class.java, Oauth2Client::class.java)
    }
}