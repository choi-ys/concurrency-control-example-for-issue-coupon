package io.study.concurrency.pessimistic.coupon.infrastructure;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepoWithPessimisticLock extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select coupon from Coupon as coupon where coupon.id = :id")
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);
}
