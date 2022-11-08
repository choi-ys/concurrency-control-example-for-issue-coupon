package io.study.concurrency.optimistic.utils;

import static io.study.concurrency.common.coupon.constants.FixtureConstants.HUNDRED;
import static io.study.concurrency.common.coupon.constants.FixtureConstants.ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.common.coupon.config.AbstractConcurrencyTestBase;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.coupon.infrastrucutre.CouponRepoWithOptimisticLock;
import io.study.concurrency.optimistic.coupon.infrastrucutre.ExhaustedCouponWithVersionEventRepo;
import io.study.concurrency.optimistic.coupon.infrastrucutre.IssuedCouponWithVersionEventRepo;
import io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator;
import javax.annotation.Resource;
import org.springframework.context.annotation.Import;

@Import(CouponWithVersionFixtureGenerator.class)
public class AbstractCouponWithVersionConcurrencyTestBase extends AbstractConcurrencyTestBase {
    @Resource
    protected CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator;

    @Resource
    private CouponRepoWithOptimisticLock couponRepoWithOptimisticLock;

    @Resource
    private IssuedCouponWithVersionEventRepo issuedCouponWithVersionEventRepo;

    @Resource
    private ExhaustedCouponWithVersionEventRepo exhaustedCouponWithVersionEventRepo;

    protected int 쿠폰_소진_이벤트_발생_횟수, 쿠폰_발급_성공_이벤트_발생_횟수, 쿠폰_발급_실패_이벤트_발생_횟수, 전체_쿠폰_발급_이벤트_발생_횟수;

    public void 쿠폰_발급_시_발생한_이벤트_조회(Long id) {
        쿠폰_소진_이벤트_발생_횟수 = exhaustedCouponWithVersionEventRepo.countByCouponId(id);
        쿠폰_발급_성공_이벤트_발생_횟수 = issuedCouponWithVersionEventRepo.countByCouponIdAndIssued(id, true);
        쿠폰_발급_실패_이벤트_발생_횟수 = issuedCouponWithVersionEventRepo.countByCouponIdAndIssued(id, false);
        전체_쿠폰_발급_이벤트_발생_횟수 = 쿠폰_발급_실패_이벤트_발생_횟수 + 쿠폰_발급_실패_이벤트_발생_횟수;
    }

    public CouponWithVersion 쿠폰_조회(Long id) {
        return couponRepoWithOptimisticLock.findById(id).orElseThrow();
    }

    protected void 발급_완료된_쿠폰_검증(Long id) {
        CouponWithVersion 조회된_쿠폰 = 쿠폰_조회(id);
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
                .isEqualTo(ONE)
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
                .isEqualTo(ONE)
        );
    }
}
