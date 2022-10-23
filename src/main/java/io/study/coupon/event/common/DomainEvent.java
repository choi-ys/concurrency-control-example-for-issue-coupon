package io.study.coupon.event.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainEvent<T> {
    private T data;
    private boolean success;

    protected DomainEvent(T data, boolean success) {
        this.data = data;
        this.success = success;
    }
}
