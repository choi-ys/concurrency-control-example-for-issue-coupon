package io.study.coupon.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Entity:Coupon")
class CouponTest {
    @Test
    @DisplayName("[예외]수량이 0보다 적은 쿠폰 생성")
    public void throwException_WhenLessThanZeroQuantity() {
        // Given
        final String name = "쿠폰";
        final int quantity = Integer.MIN_VALUE;

        // When & Then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> Coupon.of(name, quantity));
    }

    @ParameterizedTest(name = "Case[#{index}]수량 : {0} -> {1}, 발급 가능 여부 : {2}")
    @CsvSource(value = {"1:0:false", "2:1:true"}, delimiter = ':')
    @DisplayName("쿠폰 발급")
    public void issue(
        final int quantity,
        final int expected,
        final boolean issuable
    ) {
        // Given
        final String name = "쿠폰";
        final Coupon given = Coupon.of(name, quantity);

        // When
        given.issue();

        // Then
        assertThat(given.getQuantity()).isEqualTo(expected);
        assertThat(given.isIssuable()).isEqualTo(issuable);
    }

    @Test
    @DisplayName("[예외]수량이 모두 소진된 쿠폰 발급")
    public void throwException_WhenExhaustedQuantity() {
        // Given
        final String name = "쿠폰";
        final int quantity = 0;
        final Coupon given = Coupon.of(name, quantity);

        // When & Then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(given::issue);
        assertThat(given.getQuantity()).isZero();
    }
}