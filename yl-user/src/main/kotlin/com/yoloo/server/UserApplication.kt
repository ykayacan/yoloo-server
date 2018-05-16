package com.yoloo.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.scheduling.annotation.EnableScheduling

@ServletComponentScan
@EnableScheduling
@SpringBootApplication
class UserApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}