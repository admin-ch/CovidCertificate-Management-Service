package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
@ActiveProfiles({"local", "h2", "mock-signing-service", "mock-printing-service"})
@MockBean(InMemoryClientRegistrationRepository.class)
class DisplayNameModificationRepositoryIntegrationTest {
    @Autowired
    private DisplayNameModificationRepository displayNameModificationRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoDisplayNameModificationInDB_whenFindByCodeAndEntityType_thenReturnEmptyOptional() {
        // given when
        Optional<DisplayNameModification> resultOptional = displayNameModificationRepository
                .findByCodeAndEntityType("Test-Code", EntityType.VACCINE);
        // then
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    @Transactional
    void givenDisplayNameModificationInDB_whenFindByCodeAndEntityType_thenReturnDisplayNameModification() {
        // given
        String code = "Test-Code";
        persistDisplayNameModification(code, EntityType.VACCINE);
        // when
        Optional<DisplayNameModification> resultOptional = displayNameModificationRepository
                .findByCodeAndEntityType("Test-Code", EntityType.VACCINE);
        // then
        assertTrue(resultOptional.isPresent());
        assertEquals(code, resultOptional.get().getCode());
    }

    @Test
    @Transactional
    void givenNoDisplayNameModificationInDB_whenFindAll_thenReturnEmptyList() {
        // given when
        List<DisplayNameModification> result = displayNameModificationRepository.findAll();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenDisplayNameModificationsInDB_whenFindAllUvcis_thenReturnDisplayNameModifications() {
        // given
        String code = "Test-Code";
        persistDisplayNameModification(code, EntityType.VACCINE);
        persistDisplayNameModification("Test-Code2", EntityType.PROPHYLAXIS);
        // when
        Optional<DisplayNameModification> resultOptional = displayNameModificationRepository
                .findByCodeAndEntityType("Test-Code", EntityType.VACCINE);
        // then
        assertTrue(resultOptional.isPresent());
        assertEquals(code, resultOptional.get().getCode());
    }

    private void persistDisplayNameModification(String code, EntityType entityType) {
        DisplayNameModification displayNameModification = DisplayNameModification.builder()
                .code(code)
                .entityType(entityType)
                .display("Junit DisplayNameModification " + entityType.name() + " with " + code)
                .build();
        entityManager.persist(displayNameModification);
    }
}
