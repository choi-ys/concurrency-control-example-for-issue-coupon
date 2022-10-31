package io.study.concurrency.common.coupon.config;

import com.github.gavlyukovskiy.boot.jdbc.decorator.DataSourceDecoratorAutoConfiguration;
import io.study.concurrency.common.coupon.config.p6spy.P6spyLogMessageFormatConfiguration;
import java.util.function.Supplier;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DataJpaTest(showSql = false)
@ImportAutoConfiguration(DataSourceDecoratorAutoConfiguration.class)
@Import(P6spyLogMessageFormatConfiguration.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
public abstract class AbstractDataJpaTestBase {
    @Resource
    protected EntityManager entityManager;

    protected <T> T executeWithPersistContextClear(Supplier<T> supplier) {
        try {
            return supplier.get();
        } finally {
            entityManager.clear();
        }
    }

    protected void executeWithFlush(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            entityManager.flush();
        }
    }
}
