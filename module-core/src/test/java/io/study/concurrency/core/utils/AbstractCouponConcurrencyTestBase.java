package io.study.concurrency.core.utils;

import io.study.concurrency.common.coupon.config.AbstractConcurrencyTestBase;
import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.coupon.infrastructure.CouponRepo;
import io.study.concurrency.core.coupon.infrastructure.ExhaustedCouponEventRepo;
import io.study.concurrency.core.coupon.infrastructure.IssuedCouponEventRepo;
import io.study.concurrency.core.utils.fixture.CouponFixtureGenerator;
import javax.annotation.Resource;
import org.springframework.context.annotation.Import;

@Import(CouponFixtureGenerator.class)
public abstract class AbstractCouponConcurrencyTestBase extends AbstractConcurrencyTestBase {
    @Resource
    protected CouponFixtureGenerator couponFixtureGenerator;

    @Resource
    protected CouponRepo couponRepo;

    @Resource
    protected ExhaustedCouponEventRepo exhaustedCouponEventRepo;

    @Resource
    protected IssuedCouponEventRepo issuedCouponEventRepo;

    protected int 쿠폰_소진_이벤트_수, 쿠폰_발급_성공_이벤트_수, 쿠폰_발급_실패_이벤트_수, 전체_쿠폰_발급_이벤트_수;

    protected void 쿠폰_발급_시_발생한_이벤트_조회(Long couponId) {
        쿠폰_소진_이벤트_수 = exhaustedCouponEventRepo.countByCouponId(couponId);
        쿠폰_발급_성공_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, true);
        쿠폰_발급_실패_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, false);
        전체_쿠폰_발급_이벤트_수 = 쿠폰_발급_성공_이벤트_수 + 쿠폰_발급_실패_이벤트_수;
    }

    public Coupon 쿠폰_조회(Long id) {
        return couponRepo.findById(id).orElseThrow();
    }
}
