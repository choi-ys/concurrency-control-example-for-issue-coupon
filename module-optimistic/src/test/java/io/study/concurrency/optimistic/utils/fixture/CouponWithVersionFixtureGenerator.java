package io.study.concurrency.optimistic.utils.fixture;

import static io.study.concurrency.core.utils.fixture.CouponFixtureGenerator.HUNDRED;
import static io.study.concurrency.core.utils.fixture.CouponFixtureGenerator.NAME;
import static io.study.concurrency.core.utils.fixture.CouponFixtureGenerator.ONE;

import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.coupon.infrastrucutre.CouponRepoWithOptimisticLock;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestComponent
@TestConstructor(autowireMode = AutowireMode.ALL)
public class CouponWithVersionFixtureGenerator {
    private final CouponRepoWithOptimisticLock couponRepoWithOptimisticLock;

    public CouponWithVersionFixtureGenerator(CouponRepoWithOptimisticLock couponRepoWithOptimisticLock) {
        this.couponRepoWithOptimisticLock = couponRepoWithOptimisticLock;
    }

    public static CouponWithVersion 수량이_1개인_쿠폰_생성() {
        return CouponWithVersion.of(NAME, ONE);
    }

    public static CouponWithVersion 수량이_100개인_쿠폰_생성() {
        return CouponWithVersion.of(NAME, HUNDRED);
    }

    public CouponWithVersion 수량이_1개인_쿠폰_저장() {
        return 쿠폰_저장(수량이_1개인_쿠폰_생성());
    }

    public CouponWithVersion 수량이_100개인_쿠폰_저장() {
        return 쿠폰_저장(수량이_100개인_쿠폰_생성());
    }

    private CouponWithVersion 쿠폰_저장(CouponWithVersion couponWithVersion) {
        return couponRepoWithOptimisticLock.saveAndFlush(couponWithVersion);
    }
}
