package com.marlborough.domain.searchstatus;

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SearchStatsRepository : PagingAndSortingRepository<SearchStats, Long> {
    fun findByKeyword(keyword: String): Optional<SearchStats>
}