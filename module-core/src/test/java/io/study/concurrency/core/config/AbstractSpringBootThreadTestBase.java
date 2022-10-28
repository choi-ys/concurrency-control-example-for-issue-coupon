package io.study.concurrency.core.config;

import io.study.concurrency.core.coupon.infrastructure.ExhaustedCouponEventRepo;
import io.study.concurrency.core.coupon.infrastructure.IssuedCouponEventRepo;
import io.study.concurrency.core.utils.TearIsolationUtils;
import io.study.concurrency.core.utils.fixture.CouponFixtureGenerator;
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
    public static final int THREAD_POOL_SIZE = 16;
    public static final int TRY_COUNT = 100;
    public static final int ASYNC_EVENT_SUBSCRIBER_WAIT_MILLISECOND = 300;
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
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_POOL_SIZE);

        for (int i = 0; i < TRY_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    runnable.run();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        Thread.sleep(ASYNC_EVENT_SUBSCRIBER_WAIT_MILLISECOND);
    }

    protected void 쿠폰_발급_시_발생한_이벤트_조회(Long couponId) {
        쿠폰_소진_이벤트_수 = exhaustedCouponEventRepo.countByCouponId(couponId);
        쿠폰_발급_성공_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, true);
        쿠폰_발급_실패_이벤트_수 = issuedCouponEventRepo.countByCouponIdAndIssued(couponId, false);
        전체_쿠폰_발급_이벤트_수 = 쿠폰_발급_성공_이벤트_수 + 쿠폰_발급_실패_이벤트_수;
    }
}
