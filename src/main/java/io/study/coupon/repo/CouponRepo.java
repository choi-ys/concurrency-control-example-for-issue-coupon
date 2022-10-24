package io.study.coupon.repo;

import io.study.coupon.entity.Coupon;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepo extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select coupon from Coupon as coupon where coupon.id = :id")
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);
}
