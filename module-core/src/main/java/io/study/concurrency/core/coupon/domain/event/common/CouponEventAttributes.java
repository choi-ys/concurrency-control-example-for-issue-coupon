package io.study.concurrency.core.coupon.domain.event.common;

import java.time.LocalDateTime;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponEventAttributes {
    private String name;
    private String message;
    private LocalDateTime eventTime = LocalDateTime.now();

    public CouponEventAttributes(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public static CouponEventAttributes of(String name, String message) {
        return new CouponEventAttributes(name, message);
    }
}
