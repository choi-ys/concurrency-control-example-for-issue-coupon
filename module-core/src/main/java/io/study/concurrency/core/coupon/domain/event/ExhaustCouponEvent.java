package io.study.concurrency.core.coupon.domain.event;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.coupon.domain.event.common.DomainEvent;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhaustCouponEvent extends DomainEvent {
    private static final String EXHAUSTED_MESSAGE_FORMAT = "[%S]이 모두 소진되었습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private String name;
    private String message;
    private LocalDateTime eventTime = LocalDateTime.now();

    private ExhaustCouponEvent(Long couponId, String name) {
        this.couponId = couponId;
        this.name = name;
        this.message = String.format(EXHAUSTED_MESSAGE_FORMAT, name);
    }

    public static ExhaustCouponEvent of(Coupon exhaustedCoupon) {
        return new ExhaustCouponEvent(
            exhaustedCoupon.getId(),
            exhaustedCoupon.getName()
        );
    }
}
