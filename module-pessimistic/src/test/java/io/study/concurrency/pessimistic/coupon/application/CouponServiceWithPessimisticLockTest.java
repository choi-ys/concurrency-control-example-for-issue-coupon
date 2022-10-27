package io.study.concurrency.pessimistic.coupon.application;

import static io.study.concurrency.core.utils.fixture.CouponFixtureGenerator.HUNDRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.core.config.AbstractSpringBootThreadTestBase;
import io.study.concurrency.core.coupon.domain.entity.Coupon;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaSystemException;

@DisplayName("Service:Coupon:Case#2:PessimisticLock")
class CouponServiceWithPessimisticLockTest extends AbstractSpringBootThreadTestBase {
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
    @DisplayName("`JPA 비관락`을 통해 `100명의 사용자`가 `수량이 1개인 쿠폰`을 `동시에 발급`하는 경우 발생하는 `동시성 문제 해결`")
    public void concurrentControlWithJpaPessimisticLock_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_1개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_1개인_쿠폰);
        동시에_수량이_1개인_쿠폰_발급시_발생한_동시성_문제_해결_검증();
    }

    private void 동시에_수량이_1개인_쿠폰_발급시_발생한_동시성_문제_해결_검증() {
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(1),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 한번만 발생한 쿠폰 발급 이벤트")
                .isEqualTo(1),
            () -> assertThat(전체_쿠폰_발급_이벤트_수)
                .as("전체 쿠폰 발급 요청 이벤트 수")
                .isEqualTo(HUNDRED)
        );
    }

    @Test
    @DisplayName("`JPA 비관락`을 통해 `100명의 사용자`가 `수량이 100개인 쿠폰`을 `동시에 발급`하는 경우 발생하는 `동시성 문제 해결`")
    public void concurrentControlWithJpaPessimisticLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        final Coupon 수량이_100개인_쿠폰 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponService.issueCoupon(수량이_100개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_100개인_쿠폰);
        동시에_수량이_100개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(수량이_100개인_쿠폰);
    }

    private void 동시에_수량이_100개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(Coupon coupon) {
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(1),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 쿠폰 수량 만큼 발생한 쿠폰 발급 이벤트")
                .isEqualTo(coupon.getQuantity()),
            () -> assertThat(전체_쿠폰_발급_이벤트_수)
                .as("전체 쿠폰 발급 요청 이벤트 수")
                .isEqualTo(HUNDRED)
        );
    }
}
