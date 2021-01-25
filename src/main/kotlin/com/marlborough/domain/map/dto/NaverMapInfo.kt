package com.marlborough.domain.map.dto


data class NaverMapInfo(
    val lastBuildDate: String,
    val total: Long,
    val start: Long,
    val display: Long,
    val items: List<NaverItem>
)

data class NaverItem(
    val title: String,
    val link: String,
    val category: String,
    val description: String,
    val telephone: String,
    val address: String,
    val roadAddress: String,
    val mapx: Long,
    val mapy: Long
)