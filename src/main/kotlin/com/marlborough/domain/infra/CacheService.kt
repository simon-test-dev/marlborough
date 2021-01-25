package com.marlborough.domain.infra

import com.marlborough.domain.map.dto.ResultInfo

interface CacheService {
    fun getIfPresent(key: String): List<ResultInfo>?
    fun put(key: String, value: List<ResultInfo>)
}