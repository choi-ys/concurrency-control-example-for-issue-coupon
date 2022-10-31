package io.study.concurrency.common.coupon.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ObjectUtils;

public class DomainEventPublisher {
    private static ThreadLocal<ApplicationEventPublisher> publisherThreadLocal = new ThreadLocal<>();

    public static <T> void registerEvent(DomainEvent<T> event) {
        if (ObjectUtils.isEmpty(event)) {
            return;
        }

        if (publisherThreadLocal.get() != null) {
            publisherThreadLocal.get().publishEvent(event);
        }
    }

    static void setPublisher(ApplicationEventPublisher publisher) {
        publisherThreadLocal.set(publisher);
    }

    static void reset() {
        publisherThreadLocal.remove();
    }
}
