package io.study.concurrency.pessimistic.coupon.application;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.utils.AbstractCouponConcurrencyTestBase;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaSystemException;

@DisplayName("Service:Coupon:Case#3:Pessimistic Lock")
class CouponServiceWithPessimisticLockTest extends AbstractCouponConcurrencyTestBase {
    private final CouponServiceWithPessimisticLock couponService;

    public CouponServiceWithPessimisticLockTest(CouponServiceWithPessimisticLock couponService) {
        this.couponService = couponService;
    }

    @Test
    @DisplayName("동시에 실행되는 트랜잭션의 실행 순서를 제어하는 JPA 비관락의 동작")
    public void concurrentControlWithJpaPessimisticLock() {
        // Given
        Coupon 수량이_100개인_쿠폰 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When & Then
        CompletableFuture.runAsync(() -> couponService.issueCouponWithSleep(수량이_100개인_쿠폰.getId()));
        assertThatExceptionOfType(JpaSystemException.class)
            .as("JPA 비관락의 트랜잭션 실행 순서 제어로 인해 선점한 쓰레드가 트랜잭션을 종료할 때 까지 동일 자원에 트랜잭션을 시도하는 쓰레드가 대기")
            .as("지정한 시간내에 선점 쓰레드의 트랜잭션이 종료되지 않아 대기 쓰레드는 TimeoutException 발생")
            .isThrownBy(() -> couponService.issueCouponWithSleep(수량이_100개인_쿠폰.getId()));
    }

    @Test
    @DisplayName("JPA 비관락을 통해 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithJpaPessimisticLock_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_1개인_쿠폰.getId()));

        // Then
        동시에_수량이_1개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(수량이_1개인_쿠폰.getId());
    }

    @Test
    @DisplayName("JPA 비관락을 통해 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithJpaPessimisticLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        final Coupon 수량이_100개인_쿠폰 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_100개인_쿠폰.getId()));

        // Then
        동시에_수량이_100개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(수량이_100개인_쿠폰.getId());
    }
}
