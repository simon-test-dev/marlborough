package com.marlborough.domain.map

import com.marlborough.domain.map.dto.KakaoMapInfo
import com.marlborough.domain.map.dto.NaverMapInfo
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.github.resilience4j.retry.annotation.Retry
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate


@Component
class MapInfoRepository @Autowired constructor(
    private val restTemplate: RestTemplate
) {
    companion object : KLogging()
    private val KAKAO_TOKEN = "KakaoAK 90b92d4944c250c0f58e8eb1a5cd522d"
    private val NAVER_CLIENT_ID = "xtNEBq4tv6NX0uqBgNb8"
    private val NAVER_CLIENT_SECRET = "hjY0PYbjU5"
    private val KAKAO_ENTITY: HttpEntity<String>
    private val NAVER_ENTITY: HttpEntity<String>

    init {
        val kakaoHeaders = HttpHeaders()
        kakaoHeaders.set("Authorization", KAKAO_TOKEN)
        KAKAO_ENTITY = HttpEntity<String>(kakaoHeaders)

        val naverHeaders = HttpHeaders()
        naverHeaders.set("X-Naver-Client-Id", NAVER_CLIENT_ID);
        naverHeaders.set("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
        NAVER_ENTITY = HttpEntity<String>(naverHeaders)
    }

    @Retry(name = "retryService")
    @RateLimiter(name = "kakaoRestApiService")
    fun getKakaoInfo(query: String): KakaoMapInfo {
        logger.info("Kakao API Call with $query")
        return restTemplate.exchange(
            "https://dapi.kakao.com/v2/local/search/keyword.json?size=10&query=$query",
            HttpMethod.GET,
            KAKAO_ENTITY,
            KakaoMapInfo::class.java
        ).body ?: throw RestClientException("Kakao Rest API doesn't return anything")
    }

    @Retry(name = "restApiService")
    @RateLimiter(name = "naverRestApiService")
    fun getNaverInfo(query: String): NaverMapInfo {
        logger.info("Naver API Call with $query")
        return restTemplate.exchange(
            "https://openapi.naver.com/v1/search/local.json?query=$query",
            HttpMethod.GET,
            NAVER_ENTITY,
            NaverMapInfo::class.java
        ).body ?: throw RestClientException("Naver Rest API doesn't return anything")
    }
}
