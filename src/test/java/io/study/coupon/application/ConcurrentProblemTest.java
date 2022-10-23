package io.study.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.ExhaustedCouponEventRepo;
import io.study.coupon.repo.IssuedCouponEventRepo;
import io.study.utils.generator.fixture.CouponFixtureGenerator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@SpringBootTest
@Import(CouponFixtureGenerator.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@DisplayName("Service:Coupon")
@ActiveProfiles("test")
public class ConcurrentProblemTest {
    private static final int HUNDRED = 100;
    private final CouponService couponService;
    private final CouponFixtureGenerator couponFixtureGenerator;
    private final ExhaustedCouponEventRepo exhaustedCouponEventRepo;
    private final IssuedCouponEventRepo issuedCouponEventRepo;

    public ConcurrentProblemTest(
        CouponService couponService,
        CouponFixtureGenerator couponFixtureGenerator,
        ExhaustedCouponEventRepo exhaustedCouponEventRepo,
        IssuedCouponEventRepo issuedCouponEventRepo
    ) {
        this.couponService = couponService;
        this.couponFixtureGenerator = couponFixtureGenerator;
        this.exhaustedCouponEventRepo = exhaustedCouponEventRepo;
        this.issuedCouponEventRepo = issuedCouponEventRepo;
    }

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(HUNDRED);
        countDownLatch = new CountDownLatch(HUNDRED);
    }

    @Test
    @DisplayName("수량이 1개인 쿠폰을 동시에 100번 발급 요청하는 경우 발생하는 동시성 문제")
    public void occurConcurrentProblem_WhenCouponWithOneQuantityIsRequested100TimesAtTheSameTime() throws InterruptedException {
        // Given
        Coupon 수량이_1개인_쿠폰 = couponFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        동시에_100개의_쿠폰_발급_요청(수량이_1개인_쿠폰);

        // Then
        final int 쿠폰_소진_이벤트_수 = exhaustedCouponEventRepo.countByCouponId(수량이_1개인_쿠폰.getId());
        final int 쿠폰_발급_성공_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(수량이_1개인_쿠폰.getId(), true);
        final int 쿠폰_발급_실패_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(수량이_1개인_쿠폰.getId(), false);
        final int 전체_쿠폰_발급_이벤트_수 = 쿠폰_발급_성공_이벤트_수 + 쿠폰_발급_실패_이벤트_수;
        assertThat(쿠폰_소진_이벤트_수).as("동시성 문제로 인해 수량이 1개인 쿠폰이 1회 이상 소진").isNotEqualTo(1);
        assertThat(쿠폰_발급_성공_이벤트_수).as("동시성 문제로 인해 수량이 1개인 쿠폰이 1회 이상 발급 성공").isNotEqualTo(1);
        assertThat(전체_쿠폰_발급_이벤트_수).as("전체 쿠폰 발급 이벤트 수").isEqualTo(HUNDRED);
    }

    private void 동시에_100개의_쿠폰_발급_요청(Coupon 수량이_1개인_쿠폰) throws InterruptedException {
        for (int i = 0; i < HUNDRED; i++) {
            executorService.submit(() -> {
                try {
                    couponService.issueCoupon(수량이_1개인_쿠폰.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }
}
