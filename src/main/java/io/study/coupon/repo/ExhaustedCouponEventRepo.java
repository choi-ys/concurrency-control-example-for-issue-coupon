package io.study.coupon.repo;

import io.study.coupon.event.ExhaustCouponEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhaustedCouponEventRepo extends JpaRepository<ExhaustCouponEvent, Long> {
    int countByCouponId(Long couponId);
}
