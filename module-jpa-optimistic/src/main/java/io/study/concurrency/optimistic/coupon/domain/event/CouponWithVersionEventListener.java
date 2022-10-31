package io.study.concurrency.optimistic.coupon.domain.event;

import io.study.concurrency.optimistic.coupon.infrastrucutre.ExhaustedCouponWithVersionEventRepo;
import io.study.concurrency.optimistic.coupon.infrastrucutre.IssuedCouponWithVersionEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponWithVersionEventListener {
    private final IssuedCouponWithVersionEventRepo issuedCouponEventRepo;
    private final ExhaustedCouponWithVersionEventRepo exhaustedCouponEventRepo;

    @Async
    @TransactionalEventListener
    public void onIssuedEventHandler(IssuedCouponWithVersionEvent issuedCouponEvent) {
        issuedCouponEventRepo.save(issuedCouponEvent);
    }

    @Async
    @TransactionalEventListener
    public void onExhaustEventHandler(ExhaustCouponWithVersionEvent exhaustCouponEvent) {
        exhaustedCouponEventRepo.save(exhaustCouponEvent);
    }
}
