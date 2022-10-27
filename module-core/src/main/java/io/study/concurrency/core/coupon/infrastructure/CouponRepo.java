package io.study.concurrency.core.coupon.infrastructure;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepo extends JpaRepository<Coupon, Long> {
}
