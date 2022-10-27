package io.study.concurrency.core.coupon.infrastructure;

import io.study.concurrency.core.coupon.domain.event.IssuedCouponEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponEventRepo extends JpaRepository<IssuedCouponEvent, Long> {
    int countByCouponIdAndIssued(Long couponId, boolean issued);
}
