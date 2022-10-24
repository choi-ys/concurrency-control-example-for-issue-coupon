package io.study.config;

import io.study.coupon.entity.Coupon;
import io.study.coupon.repo.ExhaustedCouponEventRepo;
import io.study.coupon.repo.IssuedCouponEventRepo;
import io.study.utils.TearIsolationUtils;
import io.study.utils.generator.fixture.CouponFixtureGenerator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@ActiveProfiles("test")
@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import({CouponFixtureGenerator.class, TearIsolationUtils.class})
public abstract class AbstractSpringBootThreadTestBase {
    public static final int HUNDRED = 100;
    public static final int ASYNC_EVENT_SUBSCRIBER_WAIT_TIME = 1_000;
    protected int 쿠폰_소진_이벤트_수, 쿠폰_발급_성공_이벤트_수, 쿠폰_발급_실패_이벤트_수, 전체_쿠폰_발급_이벤트_수;

    @Resource
    protected CouponFixtureGenerator couponFixtureGenerator;

    @Resource
    protected TearIsolationUtils tearIsolationUtils;

    @Resource
    protected ExhaustedCouponEventRepo exhaustedCouponEventRepo;

    @Resource
    protected IssuedCouponEventRepo issuedCouponEventRepo;

    @BeforeEach
    void setup() {
        tearIsolationUtils.tableClear();
    }

    protected void 동시에_100개의_쿠폰_발급_요청(Runnable runnable) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(HUNDRED);
        CountDownLatch countDownLatch = new CountDownLatch(HUNDRED);

        for (int i = 0; i < HUNDRED; i++) {
            executorService.submit(() -> {
                try {
                    runnable.run();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Thread.sleep(ASYNC_EVENT_SUBSCRIBER_WAIT_TIME);
    }

    protected void 쿠폰_발급_시_발생한_이벤트_조회(Coupon coupon) {
        쿠폰_소진_이벤트_수 = exhaustedCouponEventRepo.countByCouponId(coupon.getId());
        쿠폰_발급_성공_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(coupon.getId(), true);
        쿠폰_발급_실패_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(coupon.getId(), false);
        전체_쿠폰_발급_이벤트_수 = 쿠폰_발급_성공_이벤트_수 + 쿠폰_발급_실패_이벤트_수;
    }
}
