package com.marlborough.domain.member;

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class User(
    @Id
    @GeneratedValue
    @Column(unique = true)
    val id: Long? = null,
    @Column(unique = true)
    val userId: String,
    val password: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)