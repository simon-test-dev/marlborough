package com.marlborough.domain.usersearchhistory;

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class UserSearchHistory(
    @Id
    @GeneratedValue
    @Column(unique = true)
    val id: Long? = null,
    val userId: String,
    val keyword: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)