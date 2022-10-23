package io.study.coupon.repo;

import io.study.coupon.event.IssuedCouponEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponEventRepo extends JpaRepository<IssuedCouponEvent, Long> {
    int countByCouponIdAndIssued(Long couponId, boolean issued);
}
