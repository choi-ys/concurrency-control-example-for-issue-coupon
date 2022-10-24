package io.study.coupon.repo;

import static io.study.utils.generator.fixture.CouponFixtureGenerator.수량이_100개인_쿠폰_생성;
import static org.assertj.core.api.Assertions.assertThat;

import io.study.config.AbstractDataJpaTestBase;
import io.study.coupon.entity.Coupon;
import java.util.Optional;
import nl.altindag.console.ConsoleCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("Repo:Coupon:Pessimistic Lock")
public class CouponRepoPessimisticLockTest extends AbstractDataJpaTestBase {
    private static final String PESSIMISTIC_LOCK_QUERY_KEYWORD = "for update";
    private final CouponRepo couponRepo;

    public CouponRepoPessimisticLockTest(CouponRepo couponRepo) {
        this.couponRepo = couponRepo;
    }

    @Test
    @DisplayName("JPA가 제공하는 PessimisticLock의 실행 쿼리 검증")
    public void concurrentControlByJpaPessimisticLock() {
        // Given
        final Coupon 저장된_수량이_100개인_쿠폰 = executeWithPersistContextClear(
            () -> couponRepo.save(수량이_100개인_쿠폰_생성())
        );

        // When
        Optional<String> 베타락_키워드가_포함된_실행_쿼리_조각 = executeWithAroundConsoleCaptor(
            () -> couponRepo.findByIdWithPessimisticLock(저장된_수량이_100개인_쿠폰.getId()).orElseThrow()
        );

        // Then
        assertThat(베타락_키워드가_포함된_실행_쿼리_조각.isPresent()).isTrue();
    }

    private Optional<String> executeWithAroundConsoleCaptor(Runnable runnable) {
        try (ConsoleCaptor consoleCaptor = new ConsoleCaptor()) {
            runnable.run();
            return consoleCaptor.getStandardOutput()
                .stream()
                .filter(it -> it.contains(PESSIMISTIC_LOCK_QUERY_KEYWORD))
                .findFirst();
        }
    }
}
