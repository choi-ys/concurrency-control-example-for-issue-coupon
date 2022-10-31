package io.study.concurrency.lettuce.coupon.repo;

import static io.study.concurrency.common.redis.utils.KeyGenerator.generateCouponLockKey;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LettuceLockRepo {
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean lock(Long id) {
        return redisTemplate.opsForValue()
            .setIfAbsent(
                generateCouponLockKey(id),
                id,
                Duration.ofMillis(3_000)
            );
    }

    public boolean releaseLock(Long id){
        return redisTemplate.delete(generateCouponLockKey(id));
    }
}
