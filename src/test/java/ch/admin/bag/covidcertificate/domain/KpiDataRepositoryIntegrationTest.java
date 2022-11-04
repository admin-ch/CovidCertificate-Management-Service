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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
class KpiDataRepositoryIntegrationTest {
    @Autowired
    private KpiDataRepository kpiDataRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoKpiDataInDB_whenFindByUvci_thenReturnNull() {
        // given when
        KpiData result = kpiDataRepository.findByUvci("urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E");
        // then
        assertNull(result);
    }

    @Test
    @Transactional
    void givenKpiDataInDB_whenFindByUvci_thenReturnKpiData() {
        // given
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        persistKpiData(uvci);
        // when
        KpiData result = kpiDataRepository.findByUvci(uvci);
        // then
        assertEquals(uvci, result.getUvci());
    }

    @Test
    @Transactional
    void givenNoKpiDataInDB_whenFindAll_thenReturnEmptyList() {
        // given when
        List<KpiData> result = kpiDataRepository.findAll();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenKpiDataInDB_whenFindByUvci_thenReturnOneKpiData() {
        // given
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        persistKpiData(uvci);
        persistKpiData("urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53F");
        // when
        KpiData result = kpiDataRepository.findByUvci(uvci);
        // then
        assertNotNull(result);
        assertTrue(result.getUvci().equals(uvci));
    }

    @Test
    @Transactional
    void givenKpiDataInDB_whenFindAllUvcis_thenReturnKpiData() {
        // given
        LocalDateTime from = LocalDateTime.now().minusDays(1l);
        LocalDateTime to = LocalDateTime.now().plusDays(1l);
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        persistKpiData(uvci);
        persistKpiData("urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53F");
        // when
        List<BiData> result = kpiDataRepository.findAllByDateRange(from, to);
        // then
        assertNotNull(result);
        assertEquals(2l, result.size());
    }

    private void persistKpiData(String uvci) {
        KpiData kpiData = new KpiData.KpiDataBuilder(
                LocalDateTime.now(), "z", "11223344", "API")
                .withUvci(uvci)
                .withCountry("CH")
                .withDetails("junit_kpi")
                .build();
        entityManager.persist(kpiData);
    }
}
