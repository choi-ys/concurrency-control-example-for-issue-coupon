package io.study.coupon.repo;

import static io.study.utils.generator.fixture.CouponFixtureGenerator.수량이_100개인_쿠폰_생성;
import static io.study.utils.generator.fixture.CouponFixtureGenerator.수량이_1개인_쿠폰_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.study.config.AbstractDataJpaTestBase;
import io.study.coupon.entity.Coupon;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Repo:Coupon")
class CouponRepoTest extends AbstractDataJpaTestBase {
    private final CouponRepo couponRepo;

    public CouponRepoTest(CouponRepo couponRepo) {
        this.couponRepo = couponRepo;
    }

    @Test
    @DisplayName("쿠폰 저장")
    public void save() {
        // Given
        Coupon 수량이_100개인_쿠폰 = 수량이_100개인_쿠폰_생성();

        // When
        Coupon 저장된_수량이_100개인_쿠폰 = couponRepo.save(수량이_100개인_쿠폰);

        // Then
        assertThat(저장된_수량이_100개인_쿠폰)
            .as("동일 트랜잭션 내 동일 객체 여부 검증")
            .isSameAs(수량이_100개인_쿠폰);
    }

    @Test
    @DisplayName("쿠폰 조회")
    public void findById() {
        // Given
        final Coupon 저장된_수량이_100개인_쿠폰 = executeWithPersistContextClear(
            () -> couponRepo.save(수량이_100개인_쿠폰_생성())
        );

        // When
        Coupon 조회된_수량이_100개인_쿠폰 = couponRepo.findById(저장된_수량이_100개인_쿠폰.getId()).orElseThrow();

        // Then
        assertThat(조회된_수량이_100개인_쿠폰)
            .as("객체 동등성 여부 검증")
            .isEqualTo(저장된_수량이_100개인_쿠폰);
    }

    @ParameterizedTest(name = "Case[#{index}] {0}: 발급 후 수량 : {2}, 발급 후 발급 가능 상태 : {3}")
    @MethodSource
    @DisplayName("쿠폰 발급 시 Entity 변경 감지에 의한 수량 및 상태 변경")
    public void whenIssueCouponThenUpdateQuantityAndIssuableByDirtyChecking(
        final String description,
        final Coupon given,
        final int expectedQuantity,
        final boolean expectedIssuable
    ) {
        // Given
        final Coupon 생성된_쿠폰 = executeWithPersistContextClear(() -> couponRepo.save(given));

        // When
        executeWithFlush(생성된_쿠폰::issue);

        // Then
        assertThat(생성된_쿠폰.getQuantity()).isEqualTo(expectedQuantity);
        assertThat(생성된_쿠폰.isIssuable()).isEqualTo(expectedIssuable);
    }

    private static Stream<Arguments> whenIssueCouponThenUpdateQuantityAndIssuableByDirtyChecking() {
        return Stream.of(
            Arguments.of("잔여 수량이 1개인 쿠폰 발급", 수량이_1개인_쿠폰_생성(), 0, false),
            Arguments.of("잔여 수량이 100개인 쿠폰 발급", 수량이_100개인_쿠폰_생성(), 99, true)
        );
    }
}
