package io.study.concurrency.core.coupon.domain.event.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class EventPublisherAspect {
    private final ApplicationEventPublisher eventPublisher;

    public EventPublisherAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object handleEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        DomainEventPublisher.setPublisher(eventPublisher);
        try {
            return joinPoint.proceed();
        } finally {
            DomainEventPublisher.reset();
        }
    }
}
