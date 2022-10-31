package io.study.concurrency.optimistic.coupon.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.utils.AbstractCouponWithVersionConcurrencyTestBase;
import io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(CouponWithVersionFixtureGenerator.class)
@DisplayName("Facade:Coupon:Case#2:Optimistic Lock")
class CouponFacadeWithOptimisticLockTest extends AbstractCouponWithVersionConcurrencyTestBase {
    private final CouponFacadeWithOptimisticLock couponFacadeWithOptimisticLock;

    public CouponFacadeWithOptimisticLockTest(CouponFacadeWithOptimisticLock couponFacadeWithOptimisticLock) {
        this.couponFacadeWithOptimisticLock = couponFacadeWithOptimisticLock;
    }

    @Test
    @DisplayName("JPA 낙관락을 통해 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithJpaOptimisticLock_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        CouponWithVersion 수량이_1개인_쿠폰 = couponWithVersionFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithOptimisticLock.issue(수량이_1개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_1개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(1),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 한번만 발생한 쿠폰 발급 이벤트")
                .isEqualTo(1)
        );

        CouponWithVersion 조회된_수량이_1개인_쿠폰 = 쿠폰_조회(수량이_1개인_쿠폰.getId());
        assertThat(조회된_수량이_1개인_쿠폰.isIssuable()).isFalse();
    }

    @Test
    @DisplayName("JPA 낙관락을 통해 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithJpaOptimisticLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        CouponWithVersion 수량이_100개인_쿠폰 = couponWithVersionFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithOptimisticLock.issue(수량이_100개인_쿠폰.getId()));

        // Then
        쿠폰_발급_시_발생한_이벤트_조회(수량이_100개인_쿠폰.getId());
        assertAll(
            () -> assertThat(쿠폰_소진_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어되어 한번만 발생한 쿠폰 소진 이벤트")
                .isEqualTo(1),
            () -> assertThat(쿠폰_발급_성공_이벤트_수)
                .as("적용 : 동시에 실행되는 트랜잭션의 실행 순서를 제어하기 위한 JPA의 비관락 적용")
                .as("결과 : 100명의 사용자가 수량이 1개인 쿠폰을 동시에 발급 하는 경우, 실행 순서가 제어 되어 한번만 발생한 쿠폰 발급 이벤트")
                .isEqualTo(TRY_COUNT)
        );
        CouponWithVersion 조회된_수량이_1개인_쿠폰 = 쿠폰_조회(수량이_100개인_쿠폰.getId());
        assertThat(조회된_수량이_1개인_쿠폰.isIssuable()).isFalse();
    }
}
