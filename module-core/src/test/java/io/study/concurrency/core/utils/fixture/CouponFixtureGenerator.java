package io.study.concurrency.core.utils.fixture;

import static io.study.concurrency.common.coupon.constants.FixtureConstants.HUNDRED;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.NAME;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.ONE;
import static org.hibernate.type.IntegerType.ZERO;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.coupon.infrastructure.CouponRepo;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestComponent
@TestConstructor(autowireMode = AutowireMode.ALL)
public class CouponFixtureGenerator {
    private final CouponRepo couponRepo;

    public CouponFixtureGenerator(CouponRepo couponRepo) {
        this.couponRepo = couponRepo;
    }

    public static Coupon 수량이_0개인_쿠폰_생성() {
        return Coupon.of(NAME, ZERO);
    }

    public static Coupon 수량이_1개인_쿠폰_생성() {
        return Coupon.of(NAME, ONE);
    }

    public static Coupon 수량이_100개인_쿠폰_생성() {
        return Coupon.of(NAME, HUNDRED);
    }

    public Coupon 수량이_0개인_쿠폰_저장() {
        return 쿠폰_저장(수량이_0개인_쿠폰_생성());
    }

    public Coupon 수량이_1개인_쿠폰_저장() {
        return 쿠폰_저장(수량이_1개인_쿠폰_생성());
    }

    public Coupon 수량이_100개인_쿠폰_저장() {
        return 쿠폰_저장(수량이_100개인_쿠폰_생성());
    }

    public Coupon 쿠폰_저장(Coupon coupon) {
        return couponRepo.saveAndFlush(coupon);
    }
}
