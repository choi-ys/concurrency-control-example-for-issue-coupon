package io.study.utils.generator.fixture;

import io.study.coupon.entity.Coupon;

public class CouponFixtureGenerator {
    public static final String NAME = "천원 할인 쿠폰";
    public static final int HUNDRED = 100;
    public static final int ONE = 1;

    public static Coupon 수량이_1개인_쿠폰_생성() {
        return Coupon.of(NAME, ONE);
    }

    public static Coupon 수량이_100개인_쿠폰_생성() {
        return Coupon.of(NAME, HUNDRED);
    }
}
