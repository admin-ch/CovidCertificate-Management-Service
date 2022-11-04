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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class AuthHolderRepositoryIntegrationTest {
    @Autowired
    private AuthHolderRepository authHolderRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoAuthHolderInDB_whenFindByCode_thenReturnNull() {
        // given when
        AuthHolder result = authHolderRepository.findByCode("Test-Code");
        // then
        assertNull(result);
    }

    @Test
    @Transactional
    void givenAuthHolderInDB_whenFindByCode_thenReturnAuthHolder() {
        // given
        String code = "Test-Code";
        persistAuthHolder(code);
        // when
        AuthHolder result = authHolderRepository.findByCode(code);
        // then
        assertEquals(code, result.getCode());
    }

    @Test
    @Transactional
    void givenNoAuthHolderInDB_whenFindAllCodes_thenReturnEmptyList() {
        // given when
        List<String> codes = authHolderRepository.findAllCodes();
        // then
        assertTrue(codes.isEmpty());
    }

    @Test
    @Transactional
    void givenAuthHoldersInDB_whenFindAllCodes_thenReturnAuthHolders() {
        // given
        long countBefore = authHolderRepository.count();
        String code = "Test-Code";
        persistAuthHolder(code);
        persistAuthHolder("Test-Code2");
        // when
        List<String> result = authHolderRepository.findAllCodes();
        // then
        long countExpected = countBefore + 2;
        assertEquals(countExpected, result.size());
        assertTrue(result.contains(code));
    }

    private void persistAuthHolder(String code) {
        AuthHolder authHolder = AuthHolder.builder()
                .code(code)
                .display("Junit AuthHolder " + code)
                .build();
        entityManager.persist(authHolder);
    }
}
