package com.marlborough.domain.map.dto


data class KakaoMapInfo(
    val documents: List<Document>,
    val meta: Meta
)

data class Document(
    val id: String,
    val place_name: String,
    val category_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val phone: String,
    val address_name: String,
    val road_address_name: String,
    val x: Double,
    val y: Double,
    val place_url: String,
    val distance: String,
)

data class Meta(
    val total_count: Int,
    val pageable_count: Int,
    val is_end: Boolean,
    val same_name: RegionInfo
)

data class RegionInfo(
    val keyword: String,
    val region: List<String>,
    val selected_region: String
)