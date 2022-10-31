package io.study.concurrency.redisson.coupon.facade;

import static io.study.concurrency.common.coupon.constants.FixtureConstants.HUNDRED;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.common.redis.config.AbstractRedisContainerTestBase;
import io.study.concurrency.core.coupon.domain.entity.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Redisson 락을 이용하여 동시에 실행되는 트랜잭션의 실행 순서 제어
 * - Case#1 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우 트랜잭션의 실행 순서 제어
 * - Case#2 : 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급 하는 경우 트랜잭션의 실행 순서 제어
 */
@DisplayName("Facade:Coupon:Case#5:Redisson Lock")
class CouponFacadeWithRedissonLockTest extends AbstractRedisContainerTestBase {
    private final CouponFacadeWithRedissonLock couponFacadeWithRedissonLock;

    public CouponFacadeWithRedissonLockTest(CouponFacadeWithRedissonLock couponFacadeWithRedissonLock) {
        this.couponFacadeWithRedissonLock = couponFacadeWithRedissonLock;
    }

    @Test
    @DisplayName("Redisson lock을 통해 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithLettuceSpinLock_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithRedissonLock.issueCouponWithRedissonLock(수량이_1개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_1개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_발급_성공_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 발급 이벤트가 수량만큼 발생")
                .isEqualTo(ONE),
            () -> assertThat(쿠폰_소진_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 소진 이벤트가 한번만 발생")
                .isEqualTo(ONE),
            () -> assertThat(전체_쿠폰_발급_이벤트_발생_횟수)
                .as("전체 쿠폰 발급 이벤트 횟수 : 쿠폰 발급 성공 건수 + 쿠폰 발급 실패 건수")
                .isEqualTo(TRY_COUNT)
        );
        Coupon 조회된_수량이_1개인_쿠폰 = 쿠폰_조회(수량이_1개인_쿠폰.getId());
        assertThat(조회된_수량이_1개인_쿠폰.isIssuable()).isFalse();
    }

    @Test
    @DisplayName("Redisson lock을 통해 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithLettuceSpinLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_100개인_쿠폰_저장 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithRedissonLock.issueCouponWithRedissonLock(수량이_100개인_쿠폰_저장.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_100개인_쿠폰_저장.getId());
        assertAll(
            () -> assertThat(쿠폰_발급_성공_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 발급 이벤트가 수량만큼 발생")
                .isEqualTo(HUNDRED),
            () -> assertThat(쿠폰_소진_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 소진 이벤트가 한번만 발생")
                .isEqualTo(ONE),
            () -> assertThat(전체_쿠폰_발급_이벤트_발생_횟수)
                .as("전체 쿠폰 발급 이벤트 횟수 : 쿠폰 발급 성공 건수 + 쿠폰 발급 실패 건수")
                .isEqualTo(TRY_COUNT)
        );
        Coupon 조회된_수량이_100개인_쿠폰 = 쿠폰_조회(수량이_100개인_쿠폰_저장.getId());
        assertThat(조회된_수량이_100개인_쿠폰.isIssuable()).isFalse();
    }
}
