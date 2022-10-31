package io.study.concurrency.common.redis.config;

import io.study.concurrency.core.utils.AbstractCouponConcurrencyTestBase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
public abstract class AbstractRedisContainerTestBase extends AbstractCouponConcurrencyTestBase {
    static final String REDIS_IMAGE = "redis:6-alpine";
    static final GenericContainer REDIS_CONTAINER;
    static final int REDIS_PORT = 6379;

    static {
        REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT)
            .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT));
    }
}
