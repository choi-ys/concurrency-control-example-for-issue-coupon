package io.study.coupon.application;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.CouponRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponServiceWithPessimisticLock {
    private static final int ONE_SECONDS = 5_000;
    private static final String COUPON_NOT_FOUND_ERROR_MESSAGE = "요청에 해당하는 쿠폰을 찾을 수 없습니다.";
    private final CouponRepo couponRepo;

    @Transactional
    public void issueCoupon(Long id) {
        Coupon queriedCoupon = findById(id);
        queriedCoupon.issue();
    }

    @Transactional(timeout = 1)
    public void issueCouponWithSleep(Long id) {
        Coupon queriedCoupon = findById(id);
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

    private Coupon findById(Long id) {
        return couponRepo.findByIdWithPessimisticLock(id)
            .orElseThrow(() -> new IllegalArgumentException(COUPON_NOT_FOUND_ERROR_MESSAGE));
    }
}
