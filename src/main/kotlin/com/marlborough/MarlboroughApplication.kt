package com.marlborough

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class MarlboroughApplication

fun main(args: Array<String>) {
	runApplication<MarlboroughApplication>(*args)
}
