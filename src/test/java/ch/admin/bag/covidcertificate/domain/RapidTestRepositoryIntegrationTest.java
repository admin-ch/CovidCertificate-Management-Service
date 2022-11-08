package ch.admin.bag.covidcertificate.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testDb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles({"local", "h2", "mock-signing-service", "mock-printing-service"})
@MockBean(InMemoryClientRegistrationRepository.class)
class RapidTestRepositoryIntegrationTest {

    @Autowired
    private RapidTestRepository repository;
    @PersistenceContext
    private EntityManager entityManager;


    @Test
    @Transactional
    void givenRapidTestsInDB_whenFindAllByActiveAndModifiedAtIsNot_thenReturnRapidTest() {
        // given
        final var current = LocalDateTime.now().withNano(0);
        final var modifiedAt = current.minusDays(1);

        persistRapidTest("1", true, modifiedAt);
        persistRapidTest("2", true, modifiedAt);
        persistRapidTest("3", true, current);
        // when
        List<RapidTest> results = repository.findAllByActiveAndModifiedAtIsNot(true, current);
        // then
        assertEquals(2, results.size());
    }

    @Test
    @Transactional
    void givenNotActiveRapidTestsInDB_whenFindAllByActiveAndModifiedAtIsNot_thenReturnNoTest() {
        // given
        LocalDateTime modifiedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime current = LocalDateTime.now();
        persistRapidTest("1", false, modifiedAt);
        persistRapidTest("2", false, modifiedAt);
        // when
        List<RapidTest> results = repository.findAllByActiveAndModifiedAtIsNot(true, current);
        // then
        assertEquals(0, results.size());
    }

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_one_match_of_one() {
        // given
        persistRapidTest("11",
                         true,
                         LocalDateTime.now(),
                         true);
        // when
        List<RapidTest> result = repository.findAllActiveAndChIssuable();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        RapidTest rapidTest = result.get(0);
        assertThat(rapidTest.active).isTrue();
        assertThat(rapidTest.chIssuable).isTrue();
    }

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_no_match_of_one() {
        // given
        persistRapidTest("12",
                         false,
                         LocalDateTime.now(),
                         true);
        // when
        List<RapidTest> result = repository.findAllActiveAndChIssuable();
        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @Transactional
    void findAll_ok_one_active_match_of_one() {
        // given
        persistRapidTest("13",
                         true,
                         LocalDateTime.now());
        // when
        List<RapidTest> result = repository.findAll();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        RapidTest rapidTest = result.get(0);
        assertThat(rapidTest.active).isTrue();
    }

    @Test
    @Transactional
    void findAll_ok_one_not_active_match_of_one() {
        // given
        persistRapidTest("14",
                         false,
                         LocalDateTime.now());
        // when
        List<RapidTest> result = repository.findAll();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        RapidTest rapidTest = result.get(0);
        assertThat(rapidTest.active).isFalse();
    }

    private void persistRapidTest(String code, boolean active, LocalDateTime modifiedAt) {
        RapidTest rapidTest = new RapidTest(code, "test", active, modifiedAt, null);
        entityManager.persist(rapidTest);
    }

    private void persistRapidTest(String code, boolean active, LocalDateTime modifiedAt, boolean chIssuable) {
        RapidTest rapidTest = new RapidTest(code, "test", active, modifiedAt, null);
        rapidTest.chIssuable = chIssuable;
        entityManager.persist(rapidTest);
    }
}
