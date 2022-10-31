package io.study.concurrency.optimistic.coupon.domain.event;

import io.study.concurrency.common.coupon.domain.event.CouponEventAttributes;
import io.study.concurrency.common.coupon.domain.event.DomainEvent;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
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
public class ExhaustCouponWithVersionEvent extends DomainEvent {
    private static final String EXHAUSTED_MESSAGE_FORMAT = "[%S]이 모두 소진되었습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private Long version;

    @Embedded
    private CouponEventAttributes couponEventAttributes;

    private ExhaustCouponWithVersionEvent(Long couponId, String name, Long version) {
        this.couponId = couponId;
        this.version = version;
        this.couponEventAttributes = CouponEventAttributes.of(
            name,
            String.format(EXHAUSTED_MESSAGE_FORMAT, name)
        );
    }

    public static ExhaustCouponWithVersionEvent of(CouponWithVersion exhaustedCoupon) {
        return new ExhaustCouponWithVersionEvent(
            exhaustedCoupon.getId(),
            exhaustedCoupon.getName(),
            exhaustedCoupon.getVersion()
        );
    }

    public LocalDateTime getEventTime() {
        return couponEventAttributes.getEventTime();
    }

    public String getMessage() {
        return couponEventAttributes.getMessage();
    }
}
