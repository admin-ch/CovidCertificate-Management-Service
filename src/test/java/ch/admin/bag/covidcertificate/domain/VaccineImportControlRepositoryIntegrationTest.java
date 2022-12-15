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
import java.time.LocalDate;
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
class VaccineImportControlRepositoryIntegrationTest {
    @Autowired
    private VaccineImportControlRepository vaccineImportControlRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoVaccineImportControlInDB_whenFindByImportDate_thenReturnNull() {
        // given when
        Optional<VaccineImportControl> resultOptional = vaccineImportControlRepository.findByImportDateLessThanEqualAndDoneFalse(LocalDate.now());
        // then
        assertTrue(resultOptional.isEmpty());
    }

    @Test
    @Transactional
    void givenVaccineImportControlInDB_whenFindByImportDate_thenReturnVaccineImportControl() {
        // given
        String version = "2.9.0";
        persistVaccineImportControl(version, false);
        // when
        Optional<VaccineImportControl> resultOptional = vaccineImportControlRepository.findByImportDateLessThanEqualAndDoneFalse(LocalDate.now());
        // then
        assertTrue(resultOptional.isPresent());
        assertEquals(version, resultOptional.get().getImportVersion());
    }

    @Test
    @Transactional
    void givenNoVaccineImportControlInDB_whenFindAll_thenReturnEmptyList() {
        // given when
        List<VaccineImportControl> result = vaccineImportControlRepository.findAll();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenVaccineImportControlsInDB_whenFindAllUvcis_thenReturnVaccineImportControls() {
        // given
        String version = "2.9.0";
        persistVaccineImportControl(version, true);
        persistVaccineImportControl("2.10.0", false);
        // when
        List<VaccineImportControl> result = vaccineImportControlRepository.findAll();
        // then
        assertEquals(2, result.size());
    }

    private void persistVaccineImportControl(String version, boolean done) {
        VaccineImportControl vaccineImportControl = VaccineImportControl.builder()
                .importVersion(version)
                .importDate(LocalDate.now())
                .done(done)
                .build();
        entityManager.persist(vaccineImportControl);
    }
}
