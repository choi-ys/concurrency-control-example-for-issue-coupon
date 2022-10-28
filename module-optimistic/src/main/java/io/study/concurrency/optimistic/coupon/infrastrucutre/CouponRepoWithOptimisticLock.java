package io.study.concurrency.optimistic.coupon.infrastrucutre;

import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepoWithOptimisticLock extends JpaRepository<CouponWithVersion, Long> {
    @Lock(value = LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select couponWithVersion from CouponWithVersion as couponWithVersion where couponWithVersion.id = :id")
    Optional<CouponWithVersion> findByIdWIthOptimisticLock(@Param(value = "id") Long id);
}
