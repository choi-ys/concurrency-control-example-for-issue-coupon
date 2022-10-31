package io.study.concurrency.common.coupon.domain.entity;

import static org.hibernate.type.IntegerType.ZERO;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quantity {
    public static final String LESS_THAN_ZERO_ERROR_MESSAGE = "수량은 0보다 작을 수 없습니다.";

    @Column
    private int quantity;

    private Quantity(int quantity) {
        this.quantity = quantity;
    }

    public static Quantity of(int quantity) {
        validateQuantity(quantity);
        return new Quantity(quantity);
    }

    private static void validateQuantity(int quantity) {
        if (isLessThanZero(quantity)) {
            throw new IllegalArgumentException(LESS_THAN_ZERO_ERROR_MESSAGE);
        }
    }

    private static boolean isLessThanZero(int quantity) {
        return quantity < ZERO;
    }

    public boolean isPositiveQuantity() {
        return quantity > ZERO;
    }

    public void deduct() {
        quantity -= 1;
    }

    public boolean isZeroQuantity() {
        return quantity == ZERO;
    }
}
