package io.study.concurrency.core.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.core.utils.AbstractCouponConcurrencyTestBase;
import io.study.concurrency.core.coupon.domain.entity.Coupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Service:Coupon:Case#1:Concurrent Problem")
public class CouponServiceConcurrentProblemTest extends AbstractCouponConcurrencyTestBase {
    private final CouponService couponService;

    public CouponServiceConcurrentProblemTest(CouponService couponService) {
        this.couponService = couponService;
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 수량이 1개인 쿠폰을 발급하는 경우 발생하는 동시성 문제")
    public void occurConcurrentProblem_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        final Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_1개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_1개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수).as("100번의 쿠폰 발급 요청을 처리 하는 과정에서 발생한 동시성 문제로 인해 쿠폰이 1회 이상 소진").isNotEqualTo(1),
            () -> assertThat(쿠폰_발급_성공_이벤트_수).as("100번의 쿠폰 발급 요청을 처리하는 과정에서 발생한 동시성 문제로 인해 수량이 1개인 쿠폰이 1회 이상 발급").isNotEqualTo(1),
            () -> assertThat(전체_쿠폰_발급_이벤트_수).as("전체 쿠폰 발급 이벤트 수").isEqualTo(AbstractCouponConcurrencyTestBase.TRY_COUNT)
        );
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 수량이 100개인 쿠폰을 발급하는 경우 발생하는 동시성 문제")
    public void occurConcurrentProblem_WhenCouponWithHundredsQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        final Coupon 수량이_100개인_쿠폰 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_100개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_100개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수).as("100번의 쿠폰 발급 요청을 처리하는 과정에서 발생한 동시성 문제로 인해 쿠폰 소진 이벤트 미 발생").isZero(),
            () -> assertThat(쿠폰_발급_성공_이벤트_수).as("100번의 쿠폰 발급 요청 모두 발급 성공").isEqualTo(AbstractCouponConcurrencyTestBase.TRY_COUNT),
            () -> assertThat(전체_쿠폰_발급_이벤트_수).as("전체 쿠폰 발급 이벤트 수").isEqualTo(AbstractCouponConcurrencyTestBase.TRY_COUNT)
        );
    }
}
