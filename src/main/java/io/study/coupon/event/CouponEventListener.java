package io.study.coupon.event;

import io.study.coupon.repo.ExhaustedCouponEventRepo;
import io.study.coupon.repo.IssuedCouponEventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponEventListener {
    private final IssuedCouponEventRepo issuedCouponEventRepo;
    private final ExhaustedCouponEventRepo exhaustedCouponEventRepo;

    @Async
    @EventListener
    public void onIssuedEventHandler(IssuedCouponEvent issuedCouponEvent) {
        issuedCouponEventRepo.save(issuedCouponEvent);
    }

    @Async
    @TransactionalEventListener
    public void onExhaustEventHandler(ExhaustCouponEvent exhaustCouponEvent) {
        exhaustedCouponEventRepo.save(exhaustCouponEvent);
    }
}
