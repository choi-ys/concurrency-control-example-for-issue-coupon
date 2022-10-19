package io.study.coupon.application;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.CouponRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {
    private static final String COUPON_NOT_FOUND_ERROR_MESSAGE = "요청에 해당하는 쿠폰을 찾을 수 없습니다.";
    private final CouponRepo couponRepo;

    @Transactional
    public void issueCoupon(Long id) {
        Coupon queriedCoupon = findById(id);
        queriedCoupon.issue();
        couponRepo.save(queriedCoupon);
    }

    private Coupon findById(Long id) {
        return couponRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(COUPON_NOT_FOUND_ERROR_MESSAGE));
    }
}
