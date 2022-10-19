package io.study.coupon.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class CouponEventListener {
    @Async
    @TransactionalEventListener
    public void onIssuedEventHandler(IssuedCouponEvent issuedCouponEvent) {
        log.info("[time : {}][id : {}][name : {}, quantity : {}] 쿠폰이 발급되었습니다.",
            issuedCouponEvent.getEventTime(),
            issuedCouponEvent.getId(),
            issuedCouponEvent.getName(),
            issuedCouponEvent.getQuantity()
        );
    }

    @Async
    @TransactionalEventListener
    public void onExhaustEventHandler(ExhaustCouponEvent exhaustCouponEvent) {
        log.info("[time : {}][id : {}][name : {}] 쿠폰이 소진되었습니다.",
            exhaustCouponEvent.getEventTime(),
            exhaustCouponEvent.getId(),
            exhaustCouponEvent.getName()
        );
    }
}
