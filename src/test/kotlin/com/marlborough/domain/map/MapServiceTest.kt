package com.marlborough.domain.map

import com.google.common.eventbus.EventBus
import com.marlborough.domain.infra.CacheService
import com.marlborough.domain.map.dto.*
import com.marlborough.domain.searchstatus.CountKeyword
import com.marlborough.domain.usersearchhistory.UserSearchHistoryService
import com.nhaarman.mockitokotlin2.*
import jdk.jfr.Event
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString

class MapServiceTest {
    private val mapInfoRepository: MapInfoRepository = mock()
    private val userSearchHistoryService: UserSearchHistoryService = mock()
    private val countKeywordEventBus: EventBus = mock()
    private val cacheService: CacheService = mock()
    private val mapService = MapService(mapInfoRepository, userSearchHistoryService, countKeywordEventBus, cacheService)

    @Test
    fun `데이터를 만들때 데이터 생성 히스토리 생성 CountEventBus CacheService 순으로 호출된다`() {
        whenever(mapInfoRepository.getKakaoInfo(anyString())).thenReturn(
            KakaoMapInfo(listOf(), Meta(1, 1, true, RegionInfo("", listOf(), "")))
        )
        whenever(mapInfoRepository.getNaverInfo(anyString())).thenReturn(
            NaverMapInfo("", 1L, 1L, 1L, listOf())
        )
        mapService.getMapInfo("", "")

        verify(mapInfoRepository, times(1)).getKakaoInfo(anyString())
        verify(mapInfoRepository, times(1)).getNaverInfo(anyString())
        verify(userSearchHistoryService, times(1)).save(anyString(), anyString())
        verify(countKeywordEventBus, times(1)).post(any(CountKeyword::class.java))
        verify(cacheService, times(1)).put(anyString(), anyOrNull())
    }

    @Test
    fun `아무런 결과가 없을 경우`() {
        whenever(mapInfoRepository.getKakaoInfo(anyString())).thenReturn(
            KakaoMapInfo(
                listOf(),
                Meta(1, 1, true, RegionInfo("", listOf(), ""))
            )
        )
        whenever(mapInfoRepository.getNaverInfo(anyString())).thenReturn(
            NaverMapInfo(
                "",
                1L,
                1L,
                1L,
                listOf()
            )
        )

        val result = mapService.makeData("")

        assertEquals(result.size, 0)
    }

    @Test
    fun `카카오만 존재할 경우`() {
        whenever(mapInfoRepository.getKakaoInfo(anyString())).thenReturn(
            KakaoMapInfo(
                listOf(
                    getTestDocument("1"),
                    getTestDocument("2"),
                    getTestDocument("3")
                ),
                Meta(1, 1, true, RegionInfo("", listOf(), ""))
            )
        )
        whenever(mapInfoRepository.getNaverInfo(anyString())).thenReturn(
            NaverMapInfo(
                "",
                1L,
                1L,
                1L,
                listOf()
            )
        )

        val result = mapService.makeData("")

        assertEquals(result.size, 3)
        assertEquals(result[0].title, "1")
        assertEquals(result[1].title, "2")
        assertEquals(result[2].title, "3")
    }

    @Test
    fun `네이버만 존재할 경우`() {
        whenever(mapInfoRepository.getKakaoInfo(anyString())).thenReturn(
            KakaoMapInfo(
                listOf(),
                Meta(1, 1, true, RegionInfo("", listOf(), ""))
            )
        )
        whenever(mapInfoRepository.getNaverInfo(anyString())).thenReturn(
            NaverMapInfo(
                "",
                1L,
                1L,
                1L,
                listOf(
                    getNaverItem("A"),
                    getNaverItem("B"),
                    getNaverItem("C"),
                )
            )
        )

        val result = mapService.makeData("")

        assertEquals(result.size, 3)
        assertEquals(result[0].title, "A")
        assertEquals(result[1].title, "B")
        assertEquals(result[2].title, "C")
    }


    @Test
    fun `둘 다 존재하는 경우, 카카오 기준으로 최상위에 정렬, 두 결과중 하나에만 존재, 카카오 정렬 다음 네이버 정렬`() {
        whenever(mapInfoRepository.getKakaoInfo(anyString())).thenReturn(
            KakaoMapInfo(
                listOf(
                    getTestDocument("1"),
                    getTestDocument("2"),
                    getTestDocument("3"),
                    getTestDocument("4"),
                    getTestDocument("5"),
                ),
                Meta(1, 1, true, RegionInfo("", listOf(), ""))
            )
        )
        whenever(mapInfoRepository.getNaverInfo(anyString())).thenReturn(
            NaverMapInfo(
                "",
                1L,
                1L,
                1L,
                listOf(
                    getNaverItem("A"),
                    getNaverItem("B"),
                    getNaverItem("C"),
                    getNaverItem("5"),
                    getNaverItem("4"),
                )
            )
        )

        val result = mapService.makeData("")

        assertEquals(result.size, 8)
        assertEquals(result[0].title, "4")
        assertEquals(result[1].title, "5")
        assertEquals(result[2].title, "1")
        assertEquals(result[3].title, "2")
        assertEquals(result[4].title, "3")
        assertEquals(result[5].title, "A")
        assertEquals(result[6].title, "B")
        assertEquals(result[7].title, "C")
    }

    fun getTestDocument(place_name: String): Document {
        return Document(
            place_name = place_name,
            id = "",
            category_group_code = "",
            category_name = "",
            address_name = "",
            category_group_name = "",
            distance = "",
            phone = "",
            place_url = "",
            road_address_name = "",
            x = 0.0,
            y = 0.0
        )
    }

    fun getNaverItem(title: String): NaverItem {
        return NaverItem(
            title = title,
            link = "",
            category = "",
            description = "",
            telephone = "",
            address = "",
            roadAddress = "",
            mapx = 0L,
            mapy = 0L,
        )
    }
}