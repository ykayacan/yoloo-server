package com.yoloo.server.user.infrastructure.objectify

import com.googlecode.objectify.impl.translate.TranslatorFactory
import com.yoloo.server.objectify.configuration.ObjectifyConfigurer
import com.yoloo.server.user.domain.entity.User
import com.yoloo.server.user.domain.entity.UserFilter
import com.yoloo.server.user.infrastructure.objectify.translators.CuckooFilterTranslatorFactory
import org.springframework.stereotype.Component

@Component
class UserObjectifyConfigurer : ObjectifyConfigurer {

    override fun registerObjectifyTranslators(): List<TranslatorFactory<*, *>> {
        return listOf(CuckooFilterTranslatorFactory())
    }

    override fun registerObjectifyEntities(): List<Class<*>> {
        return listOf(User::class.java, UserFilter::class.java)
    }
}