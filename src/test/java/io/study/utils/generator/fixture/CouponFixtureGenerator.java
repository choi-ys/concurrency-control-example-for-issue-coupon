package io.study.utils.generator.fixture;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.CouponRepo;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@TestComponent
@TestConstructor(autowireMode = AutowireMode.ALL)
public class CouponFixtureGenerator {
    public static final String NAME = "천원 할인 쿠폰";
    public static final int HUNDRED = 100;
    public static final int ONE = 1;

    private final CouponRepo couponRepo;

    public CouponFixtureGenerator(CouponRepo couponRepo) {
        this.couponRepo = couponRepo;
    }

    public static Coupon 수량이_1개인_쿠폰_생성() {
        return Coupon.of(NAME, ONE);
    }

    public static Coupon 수량이_100개인_쿠폰_생성() {
        return Coupon.of(NAME, HUNDRED);
    }

    public Coupon 수량이_1개인_쿠폰_저장() {
        return couponRepo.saveAndFlush(수량이_1개인_쿠폰_생성());
    }
}
