package io.study.concurrency.optimistic.coupon.infrastrucutre;

import static io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator.수량이_1개인_쿠폰_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.study.concurrency.common.coupon.config.AbstractDataJpaTestBase;
import io.study.concurrency.optimistic.coupon.domain.entity.CouponWithVersion;
import io.study.concurrency.optimistic.utils.fixture.CouponWithVersionFixtureGenerator;
import java.util.Optional;
import nl.altindag.console.ConsoleCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@DataJpaTest
@DisplayName("Repo:Coupon:Optimistic Lock")
@Import(CouponWithVersionFixtureGenerator.class)
class CouponRepoWithOptimisticLockTest extends AbstractDataJpaTestBase {
    private static final String OPTIMISTIC_LOCK_QUERY_FORMAT = "and version=%d";

    private final CouponRepoWithOptimisticLock couponRepoWithOptimisticLock;
    private final CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator;

    public CouponRepoWithOptimisticLockTest(
        CouponRepoWithOptimisticLock couponRepoWithOptimisticLock,
        CouponWithVersionFixtureGenerator couponWithVersionFixtureGenerator
    ) {
        this.couponRepoWithOptimisticLock = couponRepoWithOptimisticLock;
        this.couponWithVersionFixtureGenerator = couponWithVersionFixtureGenerator;
    }

    @Test
    @DisplayName("쿠폰 저장")
    public void save() {
        // Given
        CouponWithVersion 수량이_1개인_쿠폰 = 수량이_1개인_쿠폰_생성();

        // When
        CouponWithVersion 저장된_수량이_1개인_쿠폰 = couponRepoWithOptimisticLock.save(수량이_1개인_쿠폰);

        // Then
        assertThat(저장된_수량이_1개인_쿠폰.getId()).isNotNull();
        assertThat(저장된_수량이_1개인_쿠폰).as("동일 트랜잭션 내 객체 동일성 검증").isSameAs(수량이_1개인_쿠폰);
        assertThat(저장된_수량이_1개인_쿠폰.getVersion()).as("JPA Optimistic Lock을 이용해 동시성을 제어할 Version 컬럼의 값 생성 여부").isZero();
    }

    @Test
    @DisplayName("JPA가 제공하는 Optimistic Lock의 실행 쿼리 검증")
    public void issue() {
        // Given
        CouponWithVersion 저장된_수량이_1개인_쿠폰 = couponWithVersionFixtureGenerator.수량이_1개인_쿠폰_저장();

        // When
        Optional<String> 낙관락_키워드가_포함된_실행_쿼리_조각 = executeWithAroundConsoleCaptor(
            () -> executeWithFlush(저장된_수량이_1개인_쿠폰::issue), 저장된_수량이_1개인_쿠폰.getVersion()
        );

        // Then
        assertAll(
            () -> assertThat(저장된_수량이_1개인_쿠폰.getQuantity())
                .as("쿠폰 수량 차감 여부 검증")
                .isZero(),
            () -> assertThat(저장된_수량이_1개인_쿠폰.getVersion())
                .as("Entity 수정 시, JPA Optimistic Lock 동작에 의해 트랜잭션이 읽어온 Version의 값과 저장된 Version의 값의 동일 여부를 통해 동시성을 제어할 Version 컬럼의 값 증가 여부 검증")
                .isOne(),
            () -> assertThat(낙관락_키워드가_포함된_실행_쿼리_조각.isPresent())
                .as("Optimistic Lock을 이용한 동시성 제어를 위해 Console에 출력된 실행 쿼리의 Where절에 version 조건 포함 여부 검증")
                .isTrue()
        );
    }

    private Optional<String> executeWithAroundConsoleCaptor(Runnable runnable, Long version) {
        try (ConsoleCaptor consoleCaptor = new ConsoleCaptor()) {
            runnable.run();
            return consoleCaptor.getStandardOutput()
                .stream()
                .filter(it -> it.contains(String.format(OPTIMISTIC_LOCK_QUERY_FORMAT, version)))
                .findFirst();
        }
    }

    @Test
    @DisplayName("트랜잭션이 읽은 Version 값을 기반으로 특정 레코드의 수정을 시도할 때, update 실행 시점에 Version의 값이 이전에 읽었던 Version의 값과 일치하지 않아(Write skew) 발생하는 낙관락 충돌 예외")
    public void throwException_WhenReadEntityVersionIsNotEqualToUpdateEntityVersion() {
        // Given
        CouponWithVersion 저장된_수량이_100개인_쿠폰 = executeWithPersistContextClear(couponWithVersionFixtureGenerator::수량이_100개인_쿠폰_저장);
        쿠폰_발급(저장된_수량이_100개인_쿠폰.getId());

        // When & Then
        저장된_수량이_100개인_쿠폰.issue();
        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
            .isThrownBy(() -> couponRepoWithOptimisticLock.save(저장된_수량이_100개인_쿠폰));

        CouponWithVersion 조회된_수량이_100인_쿠폰 = couponRepoWithOptimisticLock.findById(저장된_수량이_100개인_쿠폰.getId()).orElseThrow();
        assertAll(
            () -> assertThat(조회된_수량이_100인_쿠폰.getQuantity())
                .as("낙관락 충돌 예외 발생 시, 해당 트랜잭션이 롤백되어 쿠폰 수량 미 차감 여부 검증")
                .isEqualTo(99),
            () -> assertThat(조회된_수량이_100인_쿠폰.getVersion())
                .as("낙관락 충돌 에외 발생 시, 해당 트랜잭션이 롤백되어 쿠폰 Entity의 Version 컬럼의 값 미 증가 여부 검증")
                .isOne()
        );
    }

    private void 쿠폰_발급(Long id) {
        CouponWithVersion couponWithVersion = couponRepoWithOptimisticLock.findById(id).orElseThrow();
        executeWithFlush(couponWithVersion::issue);
    }

    @Test
    @DisplayName("낙관락 충돌 예외 발생 시, ObjectOptimisticLockingFailureException에서 제공하는 Entity 식별자 정보를 이용한 재시도")
    public void retry_WhenOccurObjectOptimisticLockingFailureException() {
        // Given
        CouponWithVersion 저장된_수량이_100개인_쿠폰 = executeWithPersistContextClear(couponWithVersionFixtureGenerator::수량이_100개인_쿠폰_저장);
        쿠폰_발급(저장된_수량이_100개인_쿠폰.getId());

        // When
        저장된_수량이_100개인_쿠폰.issue();
        try {
            couponRepoWithOptimisticLock.save(저장된_수량이_100개인_쿠폰);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Long 낙관락_충돌로_인해_발급에_실패한_쿠폰_ID = (Long) exception.getIdentifier();
            쿠폰_발급(낙관락_충돌로_인해_발급에_실패한_쿠폰_ID);
        }

        // Then
        CouponWithVersion 조회된_수량이_100인_쿠폰 = couponRepoWithOptimisticLock.findById(저장된_수량이_100개인_쿠폰.getId()).orElseThrow();
        assertThat(조회된_수량이_100인_쿠폰.getQuantity()).as("재 시도로 인해 요청 수 만큼 정상적으로 발급되어 차감된 쿠폰 수량 검증").isEqualTo(98);
        assertThat(조회된_수량이_100인_쿠폰.getVersion()).as("재 시도로 인해 요청 수 만큼 증가한 Version 컬럼의 값 검증").isEqualTo(2);
    }
}
