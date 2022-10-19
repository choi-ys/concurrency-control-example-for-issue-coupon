package io.study.coupon.event;

import io.study.coupon.entity.Coupon;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuedCouponEvent {
    private Long id;
    private String name;
    private int quantity;
    private LocalDateTime eventTime = LocalDateTime.now();

    private IssuedCouponEvent(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public static IssuedCouponEvent of(Coupon issuedCoupon) {
        return new IssuedCouponEvent(
            issuedCoupon.getId(),
            issuedCoupon.getName(),
            issuedCoupon.getQuantity()
        );
    }
}
