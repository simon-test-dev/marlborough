package com.marlborough.domain.member;

import com.marlborough.domain.member.dto.UserDTO
import com.marlborough.domain.member.dto.UserForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class UserService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    fun join(userForm: UserForm): Long? {
        return userRepository.save(
            User(
                userId = userForm.userId,
                password = BCryptPasswordEncoder().encode(userForm.password)
            )
        ).id
    }

    fun findByUserId(userId: String): User {
        return userRepository.findByUserId(userId).orElseThrow { UsernameNotFoundException(userId) };
    }

    override fun loadUserByUsername(userId: String): UserDetails? {
        val member = findByUserId(userId)
        return UserDTO(member.userId, member.password)
    }
}
