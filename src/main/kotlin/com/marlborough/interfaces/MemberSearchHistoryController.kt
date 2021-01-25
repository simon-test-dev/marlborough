package com.marlborough.interfaces

import com.marlborough.domain.member.dto.UserDTO
import com.marlborough.domain.usersearchhistory.MemberSearchHistoryDTO
import com.marlborough.domain.usersearchhistory.UserSearchHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member_search_history")
class MemberSearchHistoryController @Autowired constructor(
    private val userSearchHistoryService: UserSearchHistoryService
) {
    @GetMapping
    fun getMapInfo(
        @AuthenticationPrincipal userDto: UserDTO
    ): List<MemberSearchHistoryDTO> {
        return userSearchHistoryService.findByUserId(userDto.userId)
    }
}