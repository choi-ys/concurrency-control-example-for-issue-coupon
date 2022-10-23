package io.study.coupon.event;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.study.coupon.application.CouponService;
import io.study.coupon.entity.Coupon;
import io.study.utils.generator.fixture.CouponFixtureGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SpringBootTest
@Import(CouponFixtureGenerator.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@ActiveProfiles("test")
@DisplayName("Event:Coupon")
class CouponEventListenerTest {
    @MockBean
    private CouponEventListener couponEventListener;
    private final CouponFixtureGenerator couponFixtureGenerator;
    private final CouponService couponService;

    public CouponEventListenerTest(
        CouponFixtureGenerator couponFixtureGenerator,
        CouponService couponService
    ) {
        this.couponFixtureGenerator = couponFixtureGenerator;
        this.couponService = couponService;
    }

    @Test
    @DisplayName("쿠폰 발급 이벤트 발행/소비")
    public void issuedCouponEvent() {
        // Given
        Coupon 수량이_100개인_쿠폰 = couponFixtureGenerator.수량이_100개인_쿠폰_저장();

        // When
        couponService.issueCoupon(수량이_100개인_쿠폰.getId());

        // Then
        verify(couponEventListener).onIssuedEventHandler(any(IssuedCouponEvent.class));
    }

    @Test
    @DisplayName("쿠폰 소진 이벤트 발행/소비")
    public void ExhaustedCouponEvent() {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        couponService.issueCoupon(수량이_1개인_쿠폰.getId());

        // Then
        verify(couponEventListener).onExhaustEventHandler(any(ExhaustCouponEvent.class));
        verify(couponEventListener).onIssuedEventHandler(any(IssuedCouponEvent.class));
    }

    @Test
    @DisplayName("쿠폰 발급 실패 이벤트 발행/소비")
    public void couponEvent() {
        // Given
        Coupon 수량이_0개인_쿠폰_저장 = couponFixtureGenerator.수량이_0개인_쿠폰_저장();

        // When
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> couponService.issueCoupon(수량이_0개인_쿠폰_저장.getId()));

        // Then
        verify(couponEventListener).onIssuedEventHandler(any(IssuedCouponEvent.class));
    }
}
