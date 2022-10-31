package io.study.concurrency.lettuce.coupon.facade;

import static io.study.concurrency.common.coupon.constants.FixtureConstants.HUNDRED;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.lettuce.coupon.config.AbstractRedisContainerTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Facade:Coupon:Case#4:Lettuce Spin Lock")
class CouponFacadeWithLettuceSpinLockTest extends AbstractRedisContainerTestBase {
    private final CouponFacadeWithLettuceSpinLock couponFacadeWithLettuceSpinLock;

    public CouponFacadeWithLettuceSpinLockTest(CouponFacadeWithLettuceSpinLock couponFacadeWithLettuceSpinLock) {
        this.couponFacadeWithLettuceSpinLock = couponFacadeWithLettuceSpinLock;
    }

    @Test
    @DisplayName("Lettuce를 이용한 spin lock을 통해 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithLettuceSpinLock_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithLettuceSpinLock.issueCouponWithSpinLock(수량이_1개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_1개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(ONE),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 한번만 발생한 쿠폰 발급 이벤트")
                .isEqualTo(ONE),
            () -> assertThat(전체_쿠폰_발급_이벤트_수)
                .as("전체 쿠폰 발급 요청 이벤트 수")
                .isEqualTo(TRY_COUNT)
        );
        Coupon 조회된_수량이_1개인_쿠폰 = 쿠폰_조회(수량이_1개인_쿠폰.getId());
        assertThat(조회된_수량이_1개인_쿠폰.isIssuable()).isFalse();
    }

    @Test
    @DisplayName("Lettuce를 이용한 spin lock을 통해 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithLettuceSpinLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_100개인_쿠폰_저장 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithLettuceSpinLock.issueCouponWithSpinLock(수량이_100개인_쿠폰_저장.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_100개인_쿠폰_저장.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(ONE),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 한번만 발생한 쿠폰 발급 이벤트")
                .isEqualTo(HUNDRED),
            () -> assertThat(전체_쿠폰_발급_이벤트_수)
                .as("전체 쿠폰 발급 요청 이벤트 수")
                .isEqualTo(TRY_COUNT)
        );
        Coupon 조회된_수량이_100개인_쿠폰 = 쿠폰_조회(수량이_100개인_쿠폰_저장.getId());
        assertThat(조회된_수량이_100개인_쿠폰.isIssuable()).isFalse();
    }
}
