package io.study.concurrency.core.coupon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issuable {
    @Column
    private boolean issuable;

    public Issuable(boolean issuable) {
        this.issuable = issuable;
    }

    public static Issuable of(boolean isPositiveQuantity) {
        return new Issuable(isPositiveQuantity);
    }

    public boolean isNotIssuable() {
        return !issuable;
    }

    public boolean changeUnissueable() {
        return issuable = false;
    }
}
