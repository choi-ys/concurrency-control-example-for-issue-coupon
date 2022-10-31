package io.study.concurrency.common.coupon.config;

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
@Import(TestIsolationUtils.class)
public abstract class AbstractConcurrencyTestBase {
    public static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    public static final int TRY_COUNT = 100;
    public static final int ASYNC_EVENT_SUBSCRIBER_WAIT_MILLISECOND = 1000;

    @Resource
    protected TestIsolationUtils testIsolationUtils;

    @BeforeEach
    void setup() {
        testIsolationUtils.tableClear();
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
}
