package io.study.concurrency.lettuce.coupon.repo;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.concurrency.lettuce.coupon.config.AbstractRedisContainerTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("redis-local")
@DisplayName("Repo:Redis:Lettuce")
class LettuceLockRepoTest extends AbstractRedisContainerTestBase {
    private final Long key = 1L;
    private final LettuceLockRepo lettuceLockRepo;

    public @Autowired
    LettuceLockRepoTest(LettuceLockRepo lettuceLockRepo) {
        this.lettuceLockRepo = lettuceLockRepo;
    }

    @BeforeEach
    void setUp() {
        lettuceLockRepo.releaseLock(key);
    }

    @Test
    @DisplayName("Lettuce를 이용한 lock 획득")
    public void getLock() {
        // When
        boolean getLockResult = lettuceLockRepo.lock(key);

        // Then
        assertThat(getLockResult).isTrue();
    }

    @Test
    @DisplayName("Lettuce를 이용한 lock 해제")
    public void releaseLock() {
        // Given
        boolean getLockResult = lettuceLockRepo.lock(key);
        boolean releaseLockResult = lettuceLockRepo.releaseLock(key);
        // When

        // Then
        assertThat(getLockResult).isTrue();
        assertThat(releaseLockResult).isTrue();
    }

    @Test
    @DisplayName("선점된 락 획득 시도 시, 락 획득 실패")
    public void getLockFail() {
        // When
        boolean getLockResult = lettuceLockRepo.lock(key);
        boolean getLockFailedResult = lettuceLockRepo.lock(key);

        // Then
        assertThat(getLockResult).isTrue();
        assertThat(getLockFailedResult).isFalse();
    }
}
