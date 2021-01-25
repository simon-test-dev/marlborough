package com.marlborough.domain.searchstatus;


data class SearchStatsDTO(
    val keyword: String,
    var count: Long
)