package com.marlborough.domain.searchstatus;

interface SearchStatsService {
    fun count(countKeyword: CountKeyword)
    fun findTop10(): List<SearchStatsDTO>
}