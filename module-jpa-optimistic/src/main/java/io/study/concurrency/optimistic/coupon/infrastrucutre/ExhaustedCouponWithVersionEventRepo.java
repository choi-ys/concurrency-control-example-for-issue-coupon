package io.study.concurrency.optimistic.coupon.infrastrucutre;

import io.study.concurrency.optimistic.coupon.domain.event.ExhaustCouponWithVersionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhaustedCouponWithVersionEventRepo extends JpaRepository<ExhaustCouponWithVersionEvent, Long> {
    int countByCouponId(Long couponId);
}
