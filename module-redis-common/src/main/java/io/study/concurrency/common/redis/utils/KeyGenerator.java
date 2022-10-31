package io.study.concurrency.common.redis.utils;

public class KeyGenerator {
    private static final String STOCK_LOCK_KEY_FORMAT = "coupon:%s";

    public static String generateCouponLockKey(Long id) {
        return String.format(STOCK_LOCK_KEY_FORMAT, id);
    }
}
