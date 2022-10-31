package io.study.concurrency.optimistic.coupon.application;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import io.study.concurrency.common.coupon.config.AbstractConcurrencyTestBase;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@DisplayName("Service:Coupon:Case#2:Optimistic Lock")
@Import(CouponWithVersionFixtureGenerator.class)
class CouponServiceWithOptimisticLockTest extends AbstractConcurrencyTestBase {
    private final CouponServiceWithOptimisticLock couponService;
    private final CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator;

    public CouponServiceWithOptimisticLockTest(
        CouponServiceWithOptimisticLock couponService,
        CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator
    ) {
        this.couponService = couponService;
        this.couponWithVersionFixtureGenerator = couponWithVersionFixtureGenerator;
    }

    @Test
    @DisplayName("동시에 실행되는 트랜잭션의 실행 순서를 제어하는 JPA 낙관락의 동작")
    public void concurrentControlWithJpaOptimisticLock() {
        // Given
        CouponWithVersion 수량이_100개인_쿠폰 = couponWithVersionFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        CompletableFuture.runAsync(() -> couponService.issueCouponWithSleep(수량이_100개인_쿠폰.getId()));

        // Then
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
            .as("Write skew로 인한 낙관락 충돌 예외 발생 검증")
            .isThrownBy(() -> couponService.issueCouponWithSleep(수량이_100개인_쿠폰.getId()));
    }
}
