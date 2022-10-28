package io.study.concurrency.optimistic.coupon.domain.entity;

import static io.study.concurrency.core.coupon.domain.event.common.DomainEventPublisher.registerEvent;

import io.study.concurrency.core.coupon.domain.entity.BaseEntity;
import io.study.concurrency.core.coupon.domain.entity.Issuable;
import io.study.concurrency.core.coupon.domain.entity.Quantity;
import io.study.concurrency.optimistic.coupon.domain.event.ExhaustCouponWithVersionEvent;
import io.study.concurrency.optimistic.coupon.domain.event.IssuedCouponWithVersionEvent;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponWithVersion extends BaseEntity {
    public static final String EXHAUSTED_QUANTITY_ERROR_MESSAGE = "수량이 모두 소진되어 쿠폰을 발급할 수 없습니다.";

    @Column
    private String name;

    @Embedded
    private Quantity quantity;

    @Embedded
    private Issuable issuable;

    @Version
    private Long version;

    private CouponWithVersion(String name, int quantity) {
        this.name = name;
        this.quantity = Quantity.of(quantity);
        this.issuable = Issuable.of(this.quantity.isPositiveQuantity());
    }

    public static CouponWithVersion of(String name, int quantity) {
        return new CouponWithVersion(name, quantity);
    }

    public void issue() {
        validateIssuable();
        quantity.deduct();
        registerEvent(IssuedCouponWithVersionEvent.ofSuccess(this));
        checkIsExhausted();
    }

    private void validateIssuable() {
        if (issuable.isNotIssuable()) {
            registerEvent(IssuedCouponWithVersionEvent.ofFail(this, EXHAUSTED_QUANTITY_ERROR_MESSAGE));
            throw new IllegalStateException(EXHAUSTED_QUANTITY_ERROR_MESSAGE);
        }
    }

    private void checkIsExhausted() {
        if (quantity.isZeroQuantity()) {
            issuable.changeUnissueable();
            registerEvent(ExhaustCouponWithVersionEvent.of(this));
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
        CouponWithVersion couponWithVersion = (CouponWithVersion) o;
        return Objects.equals(getId(), couponWithVersion.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
