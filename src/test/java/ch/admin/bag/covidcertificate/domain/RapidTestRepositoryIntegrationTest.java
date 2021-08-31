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
@ActiveProfiles({"local", "mock-signing-service", "mock-printing-service"})
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
        LocalDateTime modifiedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime current = LocalDateTime.now();
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

    private void persistRapidTest(String code, boolean active, LocalDateTime modifiedAt) {
        RapidTest rapidTest = new RapidTest(code, "test", active, modifiedAt);
        entityManager.persist(rapidTest);
    }
}