package com.marlborough.interfaces

import com.marlborough.domain.map.MapService
import com.marlborough.domain.member.dto.UserDTO
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/map_info")
class MapController @Autowired constructor(
    private val mapService: MapService,
) {
    @GetMapping
    @CircuitBreaker(name = "mapCircuit", fallbackMethod = "getMapInfoCache")
    fun getMapInfo(
        @RequestParam keyword: String,
        @AuthenticationPrincipal userDto: UserDTO
    ): List<String> {
        return mapService.getMapInfo(keyword, userDto.userId).map { it.title }
    }

    fun getMapInfoCache(
        keyword: String,
        userDto: UserDTO,
        e: RuntimeException
    ): List<String> {
        return mapService.getMapInfoCache(keyword).map { it.title }
    }

}