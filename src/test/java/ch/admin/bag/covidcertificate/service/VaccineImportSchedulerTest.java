package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.AuthHolderRepository;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.ProphylaxisRepository;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import ch.admin.bag.covidcertificate.domain.ValueSetUpdateLog;
import ch.admin.bag.covidcertificate.domain.ValueSetUpdateLogRepository;
import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;
import lombok.extern.slf4j.Slf4j;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
// mock-vaccine-value-sets-service as we don't test the real web request to GitHub here
@ActiveProfiles({"local", "mock-signing-service", "mock-printing-service", "mock-vaccine-value-sets-service"})
@MockBean(InMemoryClientRegistrationRepository.class)
@Slf4j
class VaccineImportSchedulerTest {

    @Autowired
    VaccineImportScheduler vaccineImportScheduler;

    @Autowired
    VaccineImportService vaccineImportService;

    @Autowired
    VaccineRepository vaccineRepository;

    @Autowired
    AuthHolderRepository authHolderRepository;

    @Autowired
    ProphylaxisRepository prophylaxisRepository;

    @Autowired
    ValueSetUpdateLogRepository valueSetUpdateLogRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void testEntitiesAfterImport() {
        // given
        this.persistVaccineImportControlNotDone();
        // when
        this.vaccineImportScheduler.processVaccineValueSetUpdate();
        // then
        this.checkVaccine();
        this.checkAuthHolder();
        this.checkProphylaxis();
        this.checkValueSetUpdateLog();
    }

    @Test
    @Transactional
    void testEntitiesWithoutImport() {
        // given
        this.persistVaccineImportControlAlreadyDone();
        // when
        this.vaccineImportScheduler.processVaccineValueSetUpdate();
        // then
        checkEntitiesAreEmpty();
    }

    private void checkVaccine() {
        List<Vaccine> vaccineList = this.vaccineRepository.findAll();
        assertNotNull(vaccineList);
        assertEquals(1, vaccineList.size(), "List of found vaccines isn't of size 1");
        Vaccine vaccine = vaccineList.get(0);
        assertTrue(vaccine.isActive(), "Vaccine is not active");
        assertTrue(vaccine.getIssuable().getCode().equalsIgnoreCase(Issuable.UNDEFINED.getCode()),
                "Vaccine code is not UNDEFINED as expected");
    }

    private void checkAuthHolder() {
        List<AuthHolder> authHolderList = this.authHolderRepository.findAll();
        assertNotNull(authHolderList);
        assertEquals(1, authHolderList.size(), "List of found auth holders isn't of size 1");
        AuthHolder authHolder = authHolderList.get(0);
        assertTrue(authHolder.isActive(), "AuthHolder is not active");
    }

    private void checkProphylaxis() {
        List<Prophylaxis> prophylaxisList = this.prophylaxisRepository.findAll();
        assertNotNull(prophylaxisList);
        assertEquals(1, prophylaxisList.size(), "List of found prophylaxis isn't of size 1");
        Prophylaxis prophylaxis = prophylaxisList.get(0);
        assertTrue(prophylaxis.isActive(), "AuthHolder is not active");
    }

    private void checkValueSetUpdateLog() {
        List<ValueSetUpdateLog> valueSetUpdateLogList = this.valueSetUpdateLogRepository.findAll();
        assertNotNull(valueSetUpdateLogList);
        assertEquals(3, valueSetUpdateLogList.size(), "List of found update log entries isn't of size 3");
        boolean vaccineMatched = false;
        boolean authHolderMatched = false;
        boolean prophylaxisMatched = false;
        for (ValueSetUpdateLog valueSetUpdateLog : valueSetUpdateLogList) {
            EntityType entityType = valueSetUpdateLog.getEntityType();
            if (entityType.getCode().equals(EntityType.VACCINE.getCode())) {
                vaccineMatched = true;
            } else if (entityType.getCode().equals(EntityType.AUTH_HOLDER.getCode())) {
                authHolderMatched = true;
            } else if (entityType.getCode().equals(EntityType.PROPHYLAXIS.getCode())) {
                prophylaxisMatched = true;
            }
            // all update actions have to be of type new
            UpdateAction updateAction = valueSetUpdateLog.getUpdateAction();
            assertEquals(updateAction.getCode(), UpdateAction.NEW.getCode(), "UpdateAction is not NEW as expected");
        }
        // three entities get updated during processing and the mock-vaccine-value-sets-service
        // delivers one of each type
        boolean result = vaccineMatched && authHolderMatched && prophylaxisMatched;
        log.info("Result details vaccineMatched={}, authHolderMatched={}, prophylaxisMatched={}",
                vaccineMatched, authHolderMatched, prophylaxisMatched);
        assertTrue(result, "Result isn't as expected. All tree types need to be matched");
    }

    private void checkEntitiesAreEmpty() {
        List<Vaccine> vaccineList = this.vaccineRepository.findAll();
        assertNotNull(vaccineList);
        assertEquals(0, vaccineList.size(), "List of found vaccines isn't empty");
        List<AuthHolder> authHolderList = this.authHolderRepository.findAll();
        assertNotNull(authHolderList);
        assertEquals(0, authHolderList.size(), "List of found auth holders isn't empty");
        List<Prophylaxis> prophylaxisList = this.prophylaxisRepository.findAll();
        assertNotNull(prophylaxisList);
        assertEquals(0, prophylaxisList.size(), "List of found prophylaxis isn't empty");
        List<ValueSetUpdateLog> valueSetUpdateLogList = this.valueSetUpdateLogRepository.findAll();
        assertNotNull(valueSetUpdateLogList);
        assertEquals(0, valueSetUpdateLogList.size(), "List of found update log entries isn't empty");
    }

    private void persistVaccineImportControlNotDone() {
        VaccineImportControl vaccineImportControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("1")
                .done(false)
                .build();
        entityManager.persist(vaccineImportControl);
    }

    private void persistVaccineImportControlAlreadyDone() {
        VaccineImportControl vaccineImportControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("1")
                .done(true)
                .build();
        entityManager.persist(vaccineImportControl);
    }
}
