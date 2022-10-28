package io.study.concurrency.optimistic.coupon.application;

import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.coupon.infrastrucutre.CouponRepoWithOptimisticLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponServiceWithOptimisticLock {
    private static final int ONE_SECONDS = 1000;
    private static final String COUPON_NOT_FOUND_ERROR_MESSAGE = "요청에 해당하는 쿠폰을 찾을 수 없습니다.";
    private final CouponRepoWithOptimisticLock couponRepo;

    @Transactional
    public void issueCoupon(Long id) {
        CouponWithVersion queriedCoupon = findById(id);
        queriedCoupon.issue();
    }

    @Transactional
    public void issueCouponWithSleep(Long id) {
        CouponWithVersion queriedCoupon = findById(id);
        queriedCoupon.issue();
        delay(ONE_SECONDS);
    }

    private void delay(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CouponWithVersion findById(Long id) {
        return couponRepo.findByIdWIthOptimisticLock(id)
            .orElseThrow(() -> new IllegalArgumentException(COUPON_NOT_FOUND_ERROR_MESSAGE));
    }
}
