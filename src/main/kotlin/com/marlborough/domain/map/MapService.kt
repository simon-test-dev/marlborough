package com.marlborough.domain.map

import com.google.common.eventbus.EventBus
import com.marlborough.domain.infra.CacheService
import com.marlborough.domain.map.dto.MapInfo
import com.marlborough.domain.map.dto.ResultInfo
import com.marlborough.domain.usersearchhistory.UserSearchHistoryService
import com.marlborough.domain.searchstatus.CountKeyword
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors
import java.util.stream.Stream

@Service
class MapService @Autowired constructor(
    private val mapInfoRepository: MapInfoRepository,
    private val userSearchHistoryService: UserSearchHistoryService,
    private val countKeywordEventBus: EventBus,
    private val cacheService: CacheService
) {
    companion object : KLogging()

    private val KAKAO_SOURCE = "KAKAO"
    private val NAVER_SOURCE = "NAVER"
    private val KAKAO_VALUE: Int = 2
    private val NAVER_VALUE: Int = 1
    private val BOTH_VALUE: Int = 3

    @Transactional//트랜잭션으로 묶어서 실패할 시에는 전체가 롤백 되도록 세팅.
    fun getMapInfo(keyword: String, userId: String): List<ResultInfo> {
        val mapInfoData = makeData(keyword)
        userSearchHistoryService.save(keyword, userId)
        countKeywordEventBus.post(CountKeyword(keyword)) // Response Time을 절감하고 관심사를 분리하기 위해서 사용함. Kafka 등으로 대체 가능.
        cacheService.put(keyword, mapInfoData)
        return mapInfoData
    }

    fun makeData(keyword: String): List<ResultInfo> {
        val kakaoInfo = getKakaoMapInfo(keyword)
        val naverInfo = getNaverMapInfo(keyword)
        return calculate(kakaoInfo, naverInfo)
    }

    private fun calculate(
        kakaoInfo: List<MapInfo>,
        naverInfo: List<MapInfo>
    ): List<ResultInfo> {
        val kakaoInfoMap = kakaoInfo.stream()
            .collect(Collectors.toMap({ it.title }, { it.id }, { it1, _ -> it1 }))
        val naverInfoMap = naverInfo.stream()
            .collect(Collectors.toMap({ it.title }, { it.id }, { it1, _ -> it1 }))

        val mergedMapInfos = Stream.of(kakaoInfo, naverInfo)
            .flatMap { it?.stream() }
            .collect(Collectors.toList())

        val infoCounter: Map<String, Int> = mergedMapInfos.stream()
            .collect(Collectors.toMap(
                { mapInfo: MapInfo -> mapInfo.title },
                { if (it.source == KAKAO_SOURCE) KAKAO_VALUE else NAVER_VALUE }
            ) { a: Int, b: Int -> a + b })

        val result = infoCounter.entries.stream()
            .map { ResultInfo(it.key, it.value) }
            .collect(Collectors.toList())

        result.sortWith { it1, it2 ->
            val decision = infoCounter[it1.title]!! - infoCounter[it2.title]!!
            when {
                decision > 0 -> -1
                decision < 0 -> 1
                else -> when (it1.score) {
                    KAKAO_VALUE -> kakaoInfoMap[it1.title]!! - kakaoInfoMap[it2.title]!!
                    NAVER_VALUE -> naverInfoMap[it1.title]!! - naverInfoMap[it2.title]!!
                    BOTH_VALUE -> kakaoInfoMap[it1.title]!! - kakaoInfoMap[it2.title]!!
                    else -> 0
                }
            }
        }
        return result
    }

    fun getMapInfoCache(keyword: String): List<ResultInfo> { // Circuit Breaker 발동 시 데이터가 없는 것보단 캐싱 되어 있는 데이터를 보여줌으로써 사용자 경험을 증진 시킨다.
        logger.info("Call cache data, keyword: $keyword")
        return cacheService.getIfPresent(keyword) ?: listOf()
    }

    private fun getNaverMapInfo(keyword: String) =
        mapInfoRepository.getNaverInfo(keyword).items.mapIndexed { index, info ->
            MapInfo(
                NAVER_SOURCE,
                index,
                info.title.removeTag(),
                info.address.getFilteredAddress()
            )
        }

    private fun getKakaoMapInfo(keyword: String) =
        mapInfoRepository.getKakaoInfo(keyword).documents.mapIndexed { index, info ->
            MapInfo(
                KAKAO_SOURCE,
                index,
                info.place_name.removeTag(),
                info.address_name.getFilteredAddress()
            )
        }


}

private fun String.removeTag(): String {
    return this.replace(Regex("</?b>"), " ").replace(" ", "")
}

private fun String.getFilteredAddress(): String {
    val split = this.split(" ")
    val dividedAddress = split.subList(1, split.size)
    return dividedAddress.stream()
        .reduce { s: String, s2: String -> "$s $s2" }
        .orElse("")
}