package io.study.coupon.event;

import io.study.coupon.entity.Coupon;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhaustCouponEvent {
    private Long id;
    private String name;
    private LocalDateTime eventTime = LocalDateTime.now();

    private ExhaustCouponEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ExhaustCouponEvent of(Coupon exhaustedCoupon) {
        return new ExhaustCouponEvent(
            exhaustedCoupon.getId(),
            exhaustedCoupon.getName()
        );
    }
}
