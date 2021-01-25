package com.marlborough.domain.usersearchhistory;

import com.marlborough.domain.member.UserRepository
import com.marlborough.domain.member.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserSearchHistoryService @Autowired constructor(
    private val userSearchHistoryRepository: UserSearchHistoryRepository
) {
    fun save(keyword: String, userId: String): MemberSearchHistoryDTO {
        return userSearchHistoryRepository.save(UserSearchHistory(userId = userId, keyword = keyword)).toDto()
    }

    fun get(id: Long): Optional<MemberSearchHistoryDTO> {
        return userSearchHistoryRepository.findById(id).map { it.toDto() }
    }

    fun findByUserId(userId: String): List<MemberSearchHistoryDTO> {
        return userSearchHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .map { it.toDto() }
    }
}
