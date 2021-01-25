package com.marlborough.domain.usersearchhistory;


data class MemberSearchHistoryDTO(
    val keyword: String,
    val createdAt: String
)

fun UserSearchHistory.toDto(): MemberSearchHistoryDTO {
    return MemberSearchHistoryDTO(this.keyword, this.createdAt.toString())
}