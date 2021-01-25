package com.marlborough.domain.config

import com.google.common.eventbus.EventBus
import jdk.jfr.Event
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean

import org.springframework.web.client.RestTemplate


@Configuration
class EventBusConfig {
    @Bean
    fun eventBus(): EventBus {
        return EventBus("keywordCounter")
    }
}

