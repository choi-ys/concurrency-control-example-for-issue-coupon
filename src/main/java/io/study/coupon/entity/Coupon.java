package io.study.coupon.entity;

import static io.study.coupon.event.common.DomainEventPublisher.registerEvent;
import static org.hibernate.type.IntegerType.ZERO;

import io.study.coupon.event.ExhaustCouponEvent;
import io.study.coupon.event.IssuedCouponEvent;
import java.util.Objects;
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
public class Coupon {
    public static final String LESS_THAN_ZERO_ERROR_MESSAGE = "수량은 0보다 작을 수 없습니다.";
    public static final String EXHAUSTED_QUANTITY_ERROR_MESSAGE = "수량이 모두 소진되어 쿠폰을 발급할 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int quantity;
    private boolean issuable;

    public Coupon(String name, int quantity) {
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
        this.issuable = isPositiveQuantity();
    }

    private void validateQuantity(int quantity) {
        if (isLessThanZero(quantity)) {
            throw new IllegalArgumentException(LESS_THAN_ZERO_ERROR_MESSAGE);
        }
    }

    private boolean isLessThanZero(int quantity) {
        return quantity < ZERO;
    }

    private boolean isPositiveQuantity() {
        return quantity > ZERO;
    }

    public static Coupon of(String name, int quantity) {
        return new Coupon(name, quantity);
    }

    public void issue() {
        validateIssuable();
        quantity -= 1;
        registerEvent(IssuedCouponEvent.ofSuccess(this));
        checkIsExhausted();
    }

    private void validateIssuable() {
        if (isNotIssuable()) {
            registerEvent(IssuedCouponEvent.ofFail(this, EXHAUSTED_QUANTITY_ERROR_MESSAGE));
            throw new IllegalStateException(EXHAUSTED_QUANTITY_ERROR_MESSAGE);
        }
    }

    private boolean isNotIssuable() {
        return !issuable;
    }

    private void checkIsExhausted() {
        if (isZeroQuantity()) {
            changeUnissueable();
            registerEvent(ExhaustCouponEvent.of(this));
        }
    }

    private boolean isZeroQuantity() {
        return quantity == ZERO;
    }

    private void changeUnissueable() {
        issuable = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
