package io.study.concurrency.core.coupon.domain.entity;

import static io.study.concurrency.core.coupon.domain.event.common.DomainEventPublisher.registerEvent;

import io.study.concurrency.core.coupon.domain.event.ExhaustCouponEvent;
import io.study.concurrency.core.coupon.domain.event.IssuedCouponEvent;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
    public static final String EXHAUSTED_QUANTITY_ERROR_MESSAGE = "수량이 모두 소진되어 쿠폰을 발급할 수 없습니다.";

    @Column
    private String name;

    @Embedded
    private Quantity quantity;

    @Embedded
    private Issuable issuable;

    private Coupon(String name, int quantity) {
        this.name = name;
        this.quantity = Quantity.of(quantity);
        this.issuable = Issuable.of(this.quantity.isPositiveQuantity());
    }

    public static Coupon of(String name, int quantity) {
        return new Coupon(name, quantity);
    }

    public void issue() {
        validateIssuable();
        quantity.deduct();
        registerEvent(IssuedCouponEvent.ofSuccess(this));
        checkIsExhausted();
    }

    private void validateIssuable() {
        if (issuable.isNotIssuable()) {
            registerEvent(IssuedCouponEvent.ofFail(this, EXHAUSTED_QUANTITY_ERROR_MESSAGE));
            throw new IllegalStateException(EXHAUSTED_QUANTITY_ERROR_MESSAGE);
        }
    }

    private void checkIsExhausted() {
        if (quantity.isZeroQuantity()) {
            issuable.changeUnissueable();
            registerEvent(ExhaustCouponEvent.of(this));
        }
    }

    public int getQuantity() {
        return quantity.getQuantity();
    }

    public boolean isIssuable() {
        return issuable.isIssuable();
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
        return Objects.equals(getId(), coupon.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
