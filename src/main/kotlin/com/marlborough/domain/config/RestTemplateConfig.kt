package com.marlborough.domain.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean

import org.springframework.web.client.RestTemplate


@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}