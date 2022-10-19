package io.study.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.type.IntegerType.ZERO;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.CouponRepo;
import io.study.utils.generator.fixture.CouponFixtureGenerator;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service:Coupon")
class CouponServiceTest {

    @Mock
    private CouponRepo couponRepo;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급")
    public void issueCoupon() {
        // Given
        final Coupon 수량이_1개인_쿠폰 = CouponFixtureGenerator.수량이_1개인_쿠폰_생성();
        쿠폰_조회_제어(수량이_1개인_쿠폰);

        // When
        couponService.issueCoupon(수량이_1개인_쿠폰.getId());

        // Then
        verify(couponRepo).findById(수량이_1개인_쿠폰.getId());
        쿠폰_발급_결과_검증(수량이_1개인_쿠폰);
    }

    private void 쿠폰_조회_제어(Coupon coupon) {
        given(couponRepo.findById(coupon.getId())).willReturn(Optional.of(coupon));
    }

    private void 쿠폰_발급_결과_검증(Coupon actual) {
        assertAll(
            () -> assertThat(actual.getQuantity()).isEqualTo(ZERO),
            () -> assertThat(actual.isIssuable()).isFalse()
        );
    }
}
