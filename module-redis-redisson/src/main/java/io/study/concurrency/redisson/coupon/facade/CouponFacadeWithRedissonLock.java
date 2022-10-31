package io.study.concurrency.redisson.coupon.facade;

import static io.study.concurrency.common.redis.utils.KeyGenerator.generateCouponLockKey;

import io.study.concurrency.core.coupon.application.CouponService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacadeWithRedissonLock {
    private final CouponService couponService;
    private final RedissonClient redissonClient;

    public void issueCouponWithRedissonLock(Long id) {
        String couponLockKey = generateCouponLockKey(id);
        RLock lock = redissonClient.getLock(couponLockKey);
        try {
            lock.tryLock(5, 1, TimeUnit.SECONDS);
            couponService.issueCoupon(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
