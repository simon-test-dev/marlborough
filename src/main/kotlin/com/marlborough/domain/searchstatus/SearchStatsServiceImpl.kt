package com.marlborough.domain.searchstatus;

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap

@Service
@Transactional
class SearchStatsServiceImpl @Autowired constructor(
    private val searchStatsRepository: SearchStatsRepository,
    eventBus: EventBus
) : SearchStatsService {

    companion object : KLogging()

    private val countMap: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    init {
        eventBus.register(this)
    }

    @Subscribe
    override fun count(countKeyword: CountKeyword) {
        logger.info { "Consume ${countKeyword.keyword}" }
        val keyword: String = countKeyword.keyword
        val keywordOptional = searchStatsRepository.findByKeyword(keyword)
        countMap.computeIfAbsent(keyword) { it ->
            searchStatsRepository.findByKeyword(it).map { it.count }.orElseGet { 0L }
        }
        countMap.compute(keyword) { _, it2: Long? ->
            val currentCount: Long = (it2 ?: 0) + 1
            val searchStats =
                keywordOptional.orElseGet { SearchStats(keyword = keyword) }
            searchStats.count(currentCount)
            searchStatsRepository.save(searchStats)
            currentCount
        }
    }

    override fun findTop10(): List<SearchStatsDTO> {
        return countMap.map { SearchStatsDTO(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(10)
    }
}