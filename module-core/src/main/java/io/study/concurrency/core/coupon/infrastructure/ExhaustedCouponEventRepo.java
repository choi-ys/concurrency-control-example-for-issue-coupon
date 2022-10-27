package io.study.concurrency.core.coupon.infrastructure;

import io.study.concurrency.core.coupon.domain.event.ExhaustCouponEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhaustedCouponEventRepo extends JpaRepository<ExhaustCouponEvent, Long> {
    int countByCouponId(Long couponId);
}
