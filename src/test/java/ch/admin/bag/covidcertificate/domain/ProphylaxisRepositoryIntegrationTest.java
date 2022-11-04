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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
class ProphylaxisRepositoryIntegrationTest {
    @Autowired
    private ProphylaxisRepository prophylaxisRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoProphylaxisInDB_whenFindByCode_thenReturnNull() {
        // given when
        Prophylaxis result = prophylaxisRepository.findByCode("Test-Code");
        // then
        assertNull(result);
    }

    @Test
    @Transactional
    void givenProphylaxisInDB_whenFindByCode_thenReturnProphylaxis() {
        // given
        String code = "Test-Code";
        persistProphylaxis(code);
        // when
        Prophylaxis result = prophylaxisRepository.findByCode(code);
        // then
        assertEquals(code, result.getCode());
    }

    @Test
    @Transactional
    void givenNoProphylaxisInDB_whenFindAllCodes_thenReturnEmptyList() {
        // given when
        List<String> result = prophylaxisRepository.findAllCodes();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenProphylaxisInDB_whenFindAllCodes_thenReturnProphylaxis() {
        // given
        String code = "Test-Code";
        persistProphylaxis(code);
        persistProphylaxis("Test-Code2");
        // when
        List<String> result = prophylaxisRepository.findAllCodes();
        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(code));
    }

    private void persistProphylaxis(String code) {
        Prophylaxis prophylaxis = Prophylaxis.builder()
                .code(code)
                .display("Junit Prophylaxis " + code)
                .build();
        entityManager.persist(prophylaxis);
    }
}
