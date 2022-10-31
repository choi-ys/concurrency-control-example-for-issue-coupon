package io.study.concurrency.optimistic.utils;

import io.study.concurrency.common.coupon.config.AbstractConcurrencyTestBase;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.coupon.infrastrucutre.CouponRepoWithOptimisticLock;
import io.study.concurrency.optimistic.coupon.infrastrucutre.ExhaustedCouponWithVersionEventRepo;
import io.study.concurrency.optimistic.coupon.infrastrucutre.IssuedCouponWithVersionEventRepo;
import io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator;
import javax.annotation.Resource;
import org.springframework.context.annotation.Import;

@Import(CouponWithVersionFixtureGenerator.class)
public class AbstractCouponWithVersionConcurrencyTestBase extends AbstractConcurrencyTestBase {
    @Resource
    protected CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator;

    @Resource
    private CouponRepoWithOptimisticLock couponRepoWithOptimisticLock;

    @Resource
    private IssuedCouponWithVersionEventRepo issuedCouponWithVersionEventRepo;

    @Resource
    private ExhaustedCouponWithVersionEventRepo exhaustedCouponWithVersionEventRepo;

    protected int 쿠폰_소진_이벤트_수, 쿠폰_발급_성공_이벤트_수, 쿠폰_발급_실패_이벤트_수, 전체_쿠폰_발급_이벤트_수;

    public void 쿠폰_발급_시_발생한_이벤트_조회(Long id) {
        쿠폰_소진_이벤트_수 = exhaustedCouponWithVersionEventRepo.countByCouponId(id);
        쿠폰_발급_성공_이벤트_수 = issuedCouponWithVersionEventRepo.countByCouponIdAndIssued(id, true);
        쿠폰_발급_실패_이벤트_수 = issuedCouponWithVersionEventRepo.countByCouponIdAndIssued(id, false);
        전체_쿠폰_발급_이벤트_수 = 쿠폰_발급_성공_이벤트_수 + 쿠폰_발급_실패_이벤트_수;
    }

    public CouponWithVersion 쿠폰_조회(Long id) {
        return couponRepoWithOptimisticLock.findById(id).orElseThrow();
    }
}
