package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.domain.KpiData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
//        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.url=jdbc:h2:mem:testDb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles({"local", "h2", "mock-signing-service", "mock-printing-service"})
@MockBean(InMemoryClientRegistrationRepository.class)
public class RevocationListReductionSchedulerIntegrationTest {

    @Autowired
    RevocationListReductionScheduler revocationListReductionScheduler;

    @Autowired
    RevocationService revocationService;

    List<String> notRevokedUvcis;

    List<String> revokedUvcis;

    List<String> notMarkedUvcis;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void beforeEachTest() {
        this.notRevokedUvcis = new ArrayList<>();
        this.revokedUvcis = new ArrayList<>();
        this.notMarkedUvcis = new ArrayList<>();
    }

    @Test
    @Transactional
    void testRevocationListBeforeDeletion() {
        // create creation KPI and prepare revocation
        createCreationKpiDataAndPrepareRevocation();
        // get list of revoked UVCIs
        List<String> listedUvcis = this.revocationService.getRevocations();
        // check this list against prepared test data
        Assertions.assertThat(listedUvcis.containsAll(revokedUvcis)).isTrue();
        Assertions.assertThat(!listedUvcis.containsAll(notRevokedUvcis)).isTrue();
    }

    @Test
    @Transactional
    void testRevocationListAfterDeletion() {
        // create creation KPI and prepare revocation
        createCreationKpiDataAndPrepareRevocation();
        // call deletion batch job on service level
        this.revocationListReductionScheduler.detectRevocationsToBeMarkedAsDeleted();
        // get list of revoked UVCIs
        List<String> listedUvcis = this.revocationService.getRevocations();
        // check this list against prepared test data
        Assertions.assertThat(listedUvcis.containsAll(notMarkedUvcis)).isTrue();
        Assertions.assertThat(!listedUvcis.containsAll(notRevokedUvcis)).isTrue();
    }

    /**
     * Method to create 3 vaccine KPI and 3 test KPI and prepare 4 of them for revocation.
     */
    private void createCreationKpiDataAndPrepareRevocation() {
        String uvci = this.persistVaccineCreationKpi();
        this.persistRevocation(uvci, false);
        this.persistRevocationKpi(uvci);
        revokedUvcis.add(uvci);

        uvci = this.persistVaccineCreationKpi();
        this.persistRevocation(uvci, true);
        this.persistRevocationKpi(uvci);
        revokedUvcis.add(uvci);
        notMarkedUvcis.add(uvci);

        uvci = this.persistTestCreationKpi();
        this.persistRevocation(uvci, false);
        this.persistRevocationKpi(uvci);
        revokedUvcis.add(uvci);

        uvci = this.persistTestCreationKpi();
        this.persistRevocation(uvci, true);
        this.persistRevocationKpi(uvci);
        revokedUvcis.add(uvci);
        notMarkedUvcis.add(uvci);

        notRevokedUvcis.add(persistVaccineCreationKpi());
        notRevokedUvcis.add(persistTestCreationKpi());
    }

    private String persistVaccineCreationKpi() {
        String uvci = FixtureCustomization.createUVCI();
        LocalDateTime kpiTimestamp = LocalDateTime.now().minusDays(35).minusHours(12).minusMinutes(30);
        KpiData kpiData = new KpiData.KpiDataBuilder(kpiTimestamp, "v", "9990888", SystemSource.WebUI.category)
                .withUvci(uvci)
                .withDetails("EU/1/20/1507")
                .withKeyIdentifier("97e52aa2d6ab2c97")
                .withCountry("CH")
                .build();
        entityManager.persist(kpiData);
        return uvci;
    }

    private String persistTestCreationKpi() {
        String uvci = FixtureCustomization.createUVCI();
        LocalDateTime kpiTimestamp = LocalDateTime.now().minusDays(32).minusHours(6).minusMinutes(15);
        KpiData kpiData = new KpiData.KpiDataBuilder(kpiTimestamp, "t", "9990888", SystemSource.WebUI.category)
                .withUvci(uvci)
                .withDetails("pcr")
                .withKeyIdentifier("d4c4c58e5b845875")
                .withCountry("CH")
                .build();
        entityManager.persist(kpiData);
        return uvci;
    }

    private void persistRevocationKpi(String uvci) {
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        KpiData kpiData = new KpiData.KpiDataBuilder(kpiTimestamp, "re", "7770666", SystemSource.WebUI.category)
                .withUvci(uvci)
                .build();
        entityManager.persist(kpiData);
    }

    private void persistRevocation(String uvci, boolean isFraud) {
        this.revocationService.createRevocation(uvci, isFraud);
    }
}
