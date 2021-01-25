package com.marlborough.domain.searchstatus;

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class SearchStats(
    @Id
    @GeneratedValue
    val id: Long? = null,
    val keyword: String,
    var count: Long = 1,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var modifiedAt: LocalDateTime = LocalDateTime.now()
) {
    fun count(currentCount: Long) {
        this.count = currentCount
        this.modifiedAt = LocalDateTime.now()
    }
}