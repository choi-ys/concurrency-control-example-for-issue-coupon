package io.study.concurrency.core.coupon.domain.event;

import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.coupon.domain.event.common.CouponEventAttributes;
import io.study.concurrency.core.coupon.domain.event.common.DomainEvent;
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
public class IssuedCouponEvent extends DomainEvent {
    private static final String ISSUED_SUCCESS_MESSAGE_FORMAT = "[%S]이 발급되었습니다.";
    private static final String ISSUED_FAIL_MESSAGE_FORMAT = "[%S] 발급에 실패하였습니다. : [%S]";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long couponId;
    private int quantity;
    private boolean issued;

    @Embedded
    private CouponEventAttributes couponEventAttributes;

    private IssuedCouponEvent(Long couponId, int quantity, boolean issued, String name, String message) {
        this.couponId = couponId;
        this.quantity = quantity;
        this.issued = issued;
        this.couponEventAttributes = CouponEventAttributes.of(name, message);
    }

    public static IssuedCouponEvent ofSuccess(Coupon issuedCoupon) {
        return new IssuedCouponEvent(
            issuedCoupon.getId(),
            issuedCoupon.getQuantity(),
            true,
            issuedCoupon.getName(),
            String.format(ISSUED_SUCCESS_MESSAGE_FORMAT, issuedCoupon.getName())
        );
    }

    public static IssuedCouponEvent ofFail(Coupon issuedCoupon, String message) {
        return new IssuedCouponEvent(
            issuedCoupon.getId(),
            issuedCoupon.getQuantity(),
            false,
            issuedCoupon.getName(),
            String.format(
                ISSUED_FAIL_MESSAGE_FORMAT,
                issuedCoupon.getName(),
                message
            )
        );
    }
}
