package io.study.concurrency.optimistic.coupon.infrastrucutre;

import io.study.concurrency.optimistic.coupon.domain.event.IssuedCouponWithVersionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponWithVersionEventRepo extends JpaRepository<IssuedCouponWithVersionEvent, Long> {
    int countByCouponIdAndIssued(Long couponId, boolean issued);
}
