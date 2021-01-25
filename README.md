##작업 외 개선 사항
###CacheService
 현재 CacheService의 경우 서버를 한대로 운용한다는 기준하에 임시로 Guava 캐시를 이용하여 캐싱을 하고 있습니다. 해당 부분은 서버가 늘어남에 따라 캐싱 데이터가 분산되는 현상을 보이게 될 것입니다. 
 분산이 이루어지면 실제로 장애가 발생해 서킷브레이커가 발동했을 경우 호출마다 데이터가 다르게 나오는 현상을 보일 수 있기 때문에 캐시 서비스를 Memcached나 Redis와 같은 데이터 저장소를 사용하는 것이 좋습니다.  

###EventBus
 Async한 데이터 처리를 위해 단일 서버에서 사용하기 쉬운 EventBus를 사용했지만 해당 부분은 RabbitMQ나 Kafka로 변경하면 서버나 리퀘스트 증가에도 더욱 유연하게 대처할 수 있습니다.

###History 
 히스토리 데이터의 경우 현재 OrderBy를 이용하여 구현했으나 Mysql에서는 해당 쿼리가 인덱스가 걸려있지 않다면 매우 헤비한 쿼리가 될 수 있기 때문에 MongoDB와 같은 Document DB에 userId를 기준으로 모아두거나 
 자주 쓰인다면 Redis Set을 이용하여 캐싱을 해두는 것이 성능 측면에서 유리합니다. 

###Retry
MSA에서는 어떠한 연유에서든지 일시적으로 비정상적인 처리가 이루어 질 수 있기 때문에 안정적으로 데이터를 가져오기 위에 Retry를 걸어두었습니다.

###Limiter
Open Api는 호출의 제한이 걸려 있을 수 있으며 Open API가 차단하기 전에 호출 레벨에서 Limiter를 걸어둠으로써 안정적으로 서비스를 운영하는 것이 중요한다.

###Circuit Breaker
우리가 호출하는 서비스가 장애 상황에 있을때 과도한 호출을 막으면서 주기적으로 해당 서비스에 대해 모니터링 하면서 해당 시간동안 서버에서 명시적으로 fallback Data를 내보내줌으로써
사용자 고객 경험을 증진시키고 안정적인 서비스가 가능합니다.

##사용된 Library

* io.github.resilience4j:resilience4j-spring-boot2:1.6.1 // Retry, Circuit Breaker, Limiter 설정을 위한 Library
* org.springframework.boot:spring-boot-starter-aop //Annotation 기반의 AOP 로직 구현을 위해 필요함.
* com.google.guava:guava:30.1-jre //EventBus, Guava Cache를 위해 사용함.
* io.github.microutils:kotlin-logging-jvm:2.0.2 //Logging을 위한 설정
* org.springframework.boot:spring-boot-devtools //h2 데이터 확인
* com.h2database:h2 //H2 InMemory DB
* com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0 // Kotlin 기반에서 Mockito 사용을 가능하게 함.
* org.mockito:mockito-inline:3.7.7  // Kotlin 기반에서 Mockito 사용을 가능하게 함.

### 호출할 CUrl 목록
// 사용자 가입
curl --location --request POST 'http://127.0.0.1/user/join' --header 'Content-Type: application/json' --data-raw '{"userId": "admin", "password": "admin"}' 

// 사용자 로그인
curl --location --request POST 'http://127.0.0.1/user/login' --header 'Content-Type: application/json' --data-raw '{"userId": "admin", "password": "admin"}' 

//사용자 로그인 시 반환된 stringToken을 쿠키 JSESSIONID에 넣어 호출 하면 호출이 정상적으로 된다.

//지도 정보
curl --location --request GET 'http://127.0.0.1/map_info?keyword=' --header 'Cookie: JSESSIONID=' 
keyword는 한글로 전송이 잘 안되기 때문에 encoding해야한다. ex)
경기도 = %EA%B2%BD%EA%B8%B0%EB%8F%84
곱창 = %EA%B3%B1%EC%B0%BD

//검색 횟수에 따른 TOP 10 정보
curl --location --request GET 'http://127.0.0.1/search_stats' --header 'Cookie: JSESSIONID=' 

// 사용자 history
curl --location --request GET 'http://127.0.0.1/member_search_history' --header 'Cookie: JSESSIONID='