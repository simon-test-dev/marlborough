package com.marlborough.domain.infra

import com.google.common.cache.CacheBuilder
import com.marlborough.domain.map.dto.ResultInfo
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class GuavaCacheService : CacheService {
    private var mapCache =
        CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build<String, List<ResultInfo>>()

    override fun getIfPresent(key: String): List<ResultInfo>? {
        return mapCache.getIfPresent(key)
    }

    override fun put(key: String, value: List<ResultInfo>) {
        return mapCache.put(key, value);
    }
}