package io.study.concurrency.core.utils;

import static io.study.concurrency.common.coupon.constants.FixtureConstants.HUNDRED;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.common.coupon.config.AbstractConcurrencyTestBase;
import io.study.concurrency.core.coupon.domain.entity.Coupon;
import io.study.concurrency.core.coupon.infrastructure.CouponRepo;
import io.study.concurrency.core.coupon.infrastructure.ExhaustedCouponEventRepo;
import io.study.concurrency.core.coupon.infrastructure.IssuedCouponEventRepo;
import io.study.concurrency.core.utils.fixture.CouponFixtureGenerator;
import javax.annotation.Resource;
import org.springframework.context.annotation.Import;

@Import(CouponFixtureGenerator.class)
public abstract class AbstractCouponConcurrencyTestBase extends AbstractConcurrencyTestBase {
    @Resource
    protected CouponFixtureGenerator couponFixtureGenerator;

    @Resource
    protected CouponRepo couponRepo;

    @Resource
    protected ExhaustedCouponEventRepo exhaustedCouponEventRepo;

    @Resource
    protected IssuedCouponEventRepo issuedCouponEventRepo;

    protected int 쿠폰_소진_이벤트_발생_횟수, 쿠폰_발급_성공_이벤트_발생_횟수, 쿠폰_발급_실패_이벤트_발생_횟수, 전체_쿠폰_발급_이벤트_발생_횟수;

    protected void 쿠폰_발급_시_발생한_이벤트_조회(Long couponId) {
        쿠폰_소진_이벤트_발생_횟수 = exhaustedCouponEventRepo.countByCouponId(couponId);
        쿠폰_발급_성공_이벤트_발생_횟수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, true);
        쿠폰_발급_실패_이벤트_발생_횟수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, false);
        전체_쿠폰_발급_이벤트_발생_횟수 = 쿠폰_발급_성공_이벤트_발생_횟수 + 쿠폰_발급_실패_이벤트_발생_횟수;
    }

    protected Coupon 쿠폰_조회(Long id) {
        return couponRepo.findById(id).orElseThrow();
    }

    protected void 발급_완료된_쿠폰_검증(Long id) {
        Coupon 조회된_쿠폰 = 쿠폰_조회(id);
        assertAll(
            () -> assertThat(조회된_쿠폰.getQuantity()).isZero(),
            () -> assertThat(조회된_쿠폰.isIssuable()).isFalse()
        );
    }

    protected void 동시에_수량이_1개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(Long id) {
        발급_완료된_쿠폰_검증(id);
        쿠폰_발급_시_발생한_이벤트_조회(id);
        assertAll(
            () -> assertThat(쿠폰_발급_성공_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 발급 이벤트가 수량만큼 발생")
                .isEqualTo(ONE),
            () -> assertThat(쿠폰_소진_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 소진 이벤트가 한번만 발생")
                .isEqualTo(ONE),
            () -> assertThat(전체_쿠폰_발급_이벤트_발생_횟수)
                .as("전체 쿠폰 발급 이벤트 횟수 : 쿠폰 발급 성공 건수 + 쿠폰 발급 실패 건수")
                .isEqualTo(TRY_COUNT)
        );
    }

    protected void 동시에_수량이_100개인_쿠폰_발급시_발생한_동시성_문제_해결_검증(Long id) {
        발급_완료된_쿠폰_검증(id);
        쿠폰_발급_시_발생한_이벤트_조회(id);
        assertAll(
            () -> assertThat(쿠폰_발급_성공_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 발급 이벤트가 수량만큼 발생")
                .isEqualTo(HUNDRED),
            () -> assertThat(쿠폰_소진_이벤트_발생_횟수)
                .as("트랜잭션의 실행 순서 제어로 인해 가장 처음 락을 획득한 쓰레드가 쿠폰 발급 로직을 수행하여, 쿠폰 소진 이벤트가 한번만 발생")
                .isEqualTo(ONE),
            () -> assertThat(전체_쿠폰_발급_이벤트_발생_횟수)
                .as("전체 쿠폰 발급 이벤트 횟수 : 쿠폰 발급 성공 건수 + 쿠폰 발급 실패 건수")
                .isEqualTo(TRY_COUNT)
        );
    }
}
