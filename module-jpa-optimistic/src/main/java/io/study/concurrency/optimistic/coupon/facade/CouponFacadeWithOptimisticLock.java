package io.study.concurrency.optimistic.coupon.facade;

import io.study.concurrency.optimistic.coupon.application.CouponServiceWithOptimisticLock;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacadeWithOptimisticLock {
    public static final int RETRY_INTERVAL_MILLISECOND = 10;
    private final CouponServiceWithOptimisticLock couponService;

    public void issueCoupon(Long id) {
        while (true) {
            if (executeWithRetry(id)) {
                break;
            }
        }
    }

    private boolean executeWithRetry(Long id) {
        try {
            couponService.issueCoupon(id);
            return true;
        } catch (ObjectOptimisticLockingFailureException exception) {
            waitForRetry();
        }
        return false;
    }

    private void waitForRetry() {
        try {
            Thread.sleep(RETRY_INTERVAL_MILLISECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
