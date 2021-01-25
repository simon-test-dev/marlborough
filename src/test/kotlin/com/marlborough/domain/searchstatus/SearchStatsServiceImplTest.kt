package com.marlborough.domain.searchstatus

import com.google.common.eventbus.EventBus
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import java.util.*

class SearchStatsServiceImplTest {
    private val searchStatsRepository: SearchStatsRepository = mock()
    private val countKeywordEventBus: EventBus = mock()
    private val mapService = SearchStatsServiceImpl(searchStatsRepository, countKeywordEventBus)

    @Test
    fun `데이터가 없으면 통계값도 없다`() {
        val findTop10 = mapService.findTop10()
        assert(findTop10.size == 0)
    }

    @Test
    fun `검색을 받아온 뒤 데이터 통계를 만든다`() {
        whenever(searchStatsRepository.findByKeyword(any())).thenReturn(Optional.empty())
        whenever(searchStatsRepository.save<SearchStats>(any())).thenReturn(SearchStats(1, "Test", 1))
        mapService.count(CountKeyword("Test"))
        val findTop10 = mapService.findTop10()
        assert(findTop10.size == 1)
        assert(findTop10[0].count == 1L)
    }

    @Test
    fun `호출되는 횟수만큼 잘 저장된다`() {
        whenever(searchStatsRepository.findByKeyword(any())).thenReturn(Optional.empty())
        whenever(searchStatsRepository.save<SearchStats>(any())).thenReturn(SearchStats(1, "Test", 1))
        mapService.count(CountKeyword("Test"))
        mapService.count(CountKeyword("Test"))
        mapService.count(CountKeyword("Test"))
        mapService.count(CountKeyword("Test"))
        mapService.count(CountKeyword("Test"))
        val findTop10 = mapService.findTop10()
        assert(findTop10.size == 1)
        assert(findTop10[0].count == 5L)
    }

    @Test
    fun `10번이 넘게 호출되면 10개 까지만 나온다`() {
        whenever(searchStatsRepository.findByKeyword(any())).thenReturn(Optional.empty())
        whenever(searchStatsRepository.save<SearchStats>(any())).thenReturn(SearchStats(1, "Test", 1))
        mapService.count(CountKeyword("1"))
        mapService.count(CountKeyword("2"))
        mapService.count(CountKeyword("3"))
        mapService.count(CountKeyword("4"))
        mapService.count(CountKeyword("5"))
        mapService.count(CountKeyword("6"))
        mapService.count(CountKeyword("7"))
        mapService.count(CountKeyword("8"))
        mapService.count(CountKeyword("9"))
        mapService.count(CountKeyword("10"))
        mapService.count(CountKeyword("11"))
        mapService.count(CountKeyword("12"))
        val findTop10 = mapService.findTop10()
        assert(findTop10.size == 10)
    }
}