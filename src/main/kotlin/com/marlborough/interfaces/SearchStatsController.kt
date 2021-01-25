package com.marlborough.interfaces

import com.marlborough.domain.searchstatus.SearchStatsDTO
import com.marlborough.domain.searchstatus.SearchStatsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search_stats")
class SearchStatsController @Autowired constructor(
    private val searchStatsServiceImpl: SearchStatsServiceImpl
) {
    @GetMapping
    fun getMapInfo(): List<SearchStatsDTO> {
        return searchStatsServiceImpl.findTop10()
    }
}