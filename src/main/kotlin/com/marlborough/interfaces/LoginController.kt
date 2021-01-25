package com.marlborough.interfaces

import com.marlborough.domain.member.User
import com.marlborough.domain.member.dto.UserDTO
import com.marlborough.domain.member.dto.UserForm
import com.marlborough.domain.member.UserService
import com.marlborough.domain.member.dto.UserResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.context.SecurityContextHolder

import org.springframework.security.web.context.HttpSessionSecurityContextRepository

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

import javax.servlet.http.HttpSession

import org.springframework.web.bind.annotation.RequestBody

import org.springframework.web.bind.annotation.RequestMapping


@RestController
@RequestMapping("/user")
class LoginController @Autowired constructor(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/join")
    fun makeMember(@RequestBody userForm: UserForm): Long? {
        return userService.join(userForm)
    }


    @PostMapping("/login")
    fun login(
        @RequestBody userDTO: UserDTO,
        session: HttpSession
    ): UserResult {
        val username: String = userDTO.username
        val password: String = userDTO.password
        val token = UsernamePasswordAuthenticationToken(username, password)
        SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(token)
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        )
        val user: User = userService.findByUserId(username)
        return UserResult(user.userId, session.id)
    }
}