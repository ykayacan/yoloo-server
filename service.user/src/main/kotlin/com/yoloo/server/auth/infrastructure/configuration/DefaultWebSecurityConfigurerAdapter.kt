package com.yoloo.server.auth.infrastructure.configuration

import com.yoloo.server.auth.infrastructure.provider.Yoloo2AuthProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class DefaultWebSecurityConfigurerAdapter(
    private val yoloo2AuthProvider: Yoloo2AuthProvider
) : WebSecurityConfigurerAdapter() {

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            //.authenticationProvider(DemoProvider())
            .authenticationProvider(yoloo2AuthProvider)
    }
}