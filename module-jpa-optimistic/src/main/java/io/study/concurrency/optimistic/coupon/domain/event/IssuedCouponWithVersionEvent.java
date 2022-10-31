package io.study.concurrency.optimistic.coupon.domain.event;

import io.study.concurrency.common.coupon.domain.event.CouponEventAttributes;
import io.study.concurrency.common.coupon.domain.event.DomainEvent;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
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
public class IssuedCouponWithVersionEvent extends DomainEvent {
    private static final String ISSUED_SUCCESS_MESSAGE_FORMAT = "[%S]이 발급되었습니다.";
    private static final String ISSUED_FAIL_MESSAGE_FORMAT = "[%S] 발급에 실패하였습니다. : [%S]";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private int quantity;
    private boolean issued;
    private Long version;

    @Embedded
    private CouponEventAttributes couponEventAttributes;

    private IssuedCouponWithVersionEvent(Long couponId, int quantity, boolean issued, Long version, String name, String message) {
        this.couponId = couponId;
        this.quantity = quantity;
        this.issued = issued;
        this.version = version;
        this.couponEventAttributes = CouponEventAttributes.of(name, message);
    }

    public static IssuedCouponWithVersionEvent ofSuccess(CouponWithVersion issuedCoupon) {
        return new IssuedCouponWithVersionEvent(
            issuedCoupon.getId(),
            issuedCoupon.getQuantity(),
            true,
            issuedCoupon.getVersion(),
            issuedCoupon.getName(),
            String.format(ISSUED_SUCCESS_MESSAGE_FORMAT, issuedCoupon.getName())
        );
    }

    public static IssuedCouponWithVersionEvent ofFail(CouponWithVersion issuedCoupon, String message) {
        return new IssuedCouponWithVersionEvent(
            issuedCoupon.getId(),
            issuedCoupon.getQuantity(),
            false,
            issuedCoupon.getVersion(),
            issuedCoupon.getName(),
            String.format(
                ISSUED_FAIL_MESSAGE_FORMAT,
                issuedCoupon.getName(),
                message
            )
        );
    }
}
