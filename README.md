동시성 제어 실습을 위한 쿠폰 발행 예제
===

# 개요
제한된 수량의 쿠폰을 동시에 발급하는 과정에서 발생할 수 있는 동시성 문제를 인지하고, 이를 해결하기 위한 다양한 접근 방법을 정리 

# 요구사항 분석
## 쿠폰
* 지정된 수량만큼 쿠폰을 `발급` 할 수 있다.
* 쿠폰은 `1회 1개씩만 발급` 할 수 있다.
* 쿠폰의 `잔여 수량이 0`인 경우 쿠폰은 `사용 불가 상태`가 된다.
* 속성 : `쿠폰 번호`, `쿠폰 이름`, `쿠폰 수량`

# 실습 환경
|분류|상세|
|---|---|
|Language|Java 11|
|Web Framework|Spring Boot 2.7.4|
|Data Access Framework|Spring Data JPA|
|DB|H2 In-memory|
|Server|Redis(Lettuce, Redisson)|
|Test|JUnit5, AssertJ, Test Container|
|Log|p6spy|

# TODO
* [ ] 쿠폰 Entity 설계 및 JPA Repository 생성
* [ ] 쿠폰 재고 감소 로직 구현
* [ ] 쿠폰 발급/소진 이벤트 발행
* [ ] 쿠폰 재고 소진 시 사용 가능 상태 변경 event 구현 
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, 발생하는 동시성 문제 발생 TC 작성
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, Java synchronized를 이용한 동시성 제어 TC 작성
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, JPA Pessimistic Lock을 이용한 동시성 제어 TC 작성
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, JPA Optimistic Lock을 이용한 동시성 제어 TC 작성
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, Redis Lettuce의 Spin Lock을 이용한 동시성 제어 TC 작성
* [ ] 동시에 여러 쓰레드가 단일 쿠폰을 발급 받는 경우, Redis Redisson을 이용한 동시성 제어 TC 작성

---
