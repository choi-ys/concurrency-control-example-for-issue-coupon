package io.study.concurrency.lettuce.coupon.facade;

import io.study.concurrency.core.coupon.application.CouponService;
import io.study.concurrency.lettuce.coupon.repo.LettuceLockRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacadeWithLettuceSpinLock {
    public static final int RETRY_INTERVAL_MILLISECOND = 10;
    private final CouponService couponService;
    private final LettuceLockRepo lettuceLockRepo;

    public void issueCouponWithSpinLock(Long id) {
        getLock(id);

        try {
            couponService.issueCoupon(id);
        } finally {
            lettuceLockRepo.releaseLock(id);
        }
    }

    private void getLock(Long id) {
        while (!lettuceLockRepo.lock(id)) {
            waitForRetry();
        }
    }

    private void waitForRetry() {
        try {
            Thread.sleep(RETRY_INTERVAL_MILLISECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
