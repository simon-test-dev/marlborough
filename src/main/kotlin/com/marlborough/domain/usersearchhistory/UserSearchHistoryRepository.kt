package com.marlborough.domain.usersearchhistory;

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSearchHistoryRepository : PagingAndSortingRepository<UserSearchHistory, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: String): List<UserSearchHistory>
}