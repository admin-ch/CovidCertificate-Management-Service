package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;
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
class ValueSetUpdateLogRepositoryIntegrationTest {
    @Autowired
    private ValueSetUpdateLogRepository valueSetUpdateLogRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoValueSetUpdateLogInDB_whenFindByCode_thenReturnNull() {
        // given when
        ValueSetUpdateLog result = valueSetUpdateLogRepository.findByCode("Test-Code");
        // then
        assertNull(result);
    }

    @Test
    @Transactional
    void givenValueSetUpdateLogInDB_whenFindByCode_thenReturnValueSetUpdateLog() {
        // given
        String code = "Test-Code";
        persistValueSetUpdateLog(code, EntityType.VACCINE);
        // when
        ValueSetUpdateLog result = valueSetUpdateLogRepository.findByCode(code);
        // then
        assertEquals(code, result.getCode());
    }

    @Test
    @Transactional
    void givenNoValueSetUpdateLogInDB_whenFindAllCodes_thenReturnEmptyList() {
        // given when
        List<String> result = valueSetUpdateLogRepository.findAllCodes();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenValueSetUpdateLogsInDB_whenFindAllCodes_thenReturnValueSetUpdateLogs() {
        // given
        String code = "Test-Code";
        persistValueSetUpdateLog(code, EntityType.VACCINE);
        persistValueSetUpdateLog("Test-Code2", EntityType.PROPHYLAXIS);
        // when
        List<String> result = valueSetUpdateLogRepository.findAllCodes();
        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(code));
    }

    private void persistValueSetUpdateLog(String code, EntityType entityType) {
        ValueSetUpdateLog valueSetUpdateLog = ValueSetUpdateLog.builder()
                .code(code)
                .entityType(entityType)
                .updatedAt(LocalDateTime.now())
                .updateAction(UpdateAction.UPDATE)
                .build();
        entityManager.persist(valueSetUpdateLog);
    }
}
