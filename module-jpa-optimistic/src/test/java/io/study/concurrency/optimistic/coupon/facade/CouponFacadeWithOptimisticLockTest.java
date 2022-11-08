package io.study.concurrency.optimistic.coupon.facade;

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
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithOptimisticLock.issueCoupon(수량이_1개인_쿠폰.getId()));

        // Then
        동시에_수량이_1개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(수량이_1개인_쿠폰.getId());
    }

    @Test
    @DisplayName("JPA 낙관락을 통해 100명의 사용자가 수량이 100개인 쿠폰을 동시에 발급하는 경우 발생하는 동시성 문제 해결")
    public void concurrentControlWithJpaOptimisticLock_WhenCouponWithHundredQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        CouponWithVersion 수량이_100개인_쿠폰 = couponWithVersionFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(() -> couponFacadeWithOptimisticLock.issueCoupon(수량이_100개인_쿠폰.getId()));

        // Then
        동시에_수량이_100개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(수량이_100개인_쿠폰.getId());
    }
}
