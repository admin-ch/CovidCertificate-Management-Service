package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.api.request.Issuable;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
class VaccineRepositoryIntegrationTest {
    @Autowired
    private VaccineRepository vaccineRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_one_match_of_one() {
        // given
        persistVaccine("EU/1/20/1528",
                       "Comirnaty",
                       true,
                       true,
                       "1119349007",
                       "SARS-CoV-2 mRNA vaccine",
                       true,
                       "ORG-100030215",
                       "Biontech Manufacturing GmbH",
                       true,
                       200,
                       false,
                       true,
                       false);
        // when
        List<Vaccine> result = vaccineRepository.findAllGatewayApiActive();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getIssuable()).isEqualTo(Issuable.CH_ONLY);
    }

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_no_match_of_four() {
        // given
        persistVaccine("EU/1/20/0001",
                       "Test not active",
                       false,
                       true,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0002",
                       "Test not active",
                       false,
                       true,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);

        persistVaccine("EU/1/20/0003",
                       "Test not active",
                       false,
                       true,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);

        persistVaccine("EU/1/20/0004",
                       "Test not active",
                       false,
                       true,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);

        // when
        List<Vaccine> result = vaccineRepository.findAllGatewayApiActive();
        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_one_match_of_four() {
        // given
        persistVaccine("EU/1/20/0005",
                       "Test active",
                       true,
                       true,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       false,
                       false);
        persistVaccine("EU/1/20/0006",
                       "Test active",
                       true,
                       true,
                       "1119349007",
                       "Prophylaxis not active",
                       true,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0007",
                       "Test active",
                       true,
                       true,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company active",
                       false,
                       200,
                       false,
                       false,
                       false);
        persistVaccine("EU/1/20/0008",
                       "Test active",
                       true,
                       true,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       false,
                       false);
        // when
        List<Vaccine> result = vaccineRepository.findAllGatewayApiActive();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getProphylaxis().isActive()).isTrue();
        assertThat(vaccine.getAuthHolder().isActive()).isTrue();
    }

    @Test
    @Transactional
    void findAll_ok_one_match_of_one() {
        // given
        persistVaccine("EU/1/20/1528",
                       "Comirnaty",
                       true,
                       false,
                       "1119349007",
                       "SARS-CoV-2 mRNA vaccine",
                       true,
                       "ORG-100030215",
                       "Biontech Manufacturing GmbH",
                       false,
                       200,
                       false,
                       true,
                       false);
        // when
        List<Vaccine> result = vaccineRepository.findAll();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
    }

    @Test
    @Transactional
    void findAll_ok_four_match_of_four() {
        // given
        persistVaccine("EU/1/20/0001",
                       "Test not active 01",
                       false,
                       false,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0002",
                       "Test not active 02",
                       false,
                       false,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0003",
                       "Test not active 03",
                       false,
                       false,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0004",
                       "Test not active 04",
                       false,
                       false,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);
        // when
        List<Vaccine> result = vaccineRepository.findAll();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(4);

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isFalse();
        assertThat(vaccine.getProphylaxis().isActive()).isTrue();
        assertThat(vaccine.getAuthHolder().isActive()).isFalse();

        vaccine = result.get(1);
        assertThat(vaccine.isActive()).isFalse();
        assertThat(vaccine.getProphylaxis().isActive()).isFalse();
        assertThat(vaccine.getAuthHolder().isActive()).isTrue();

        vaccine = result.get(2);
        assertThat(vaccine.isActive()).isFalse();
        assertThat(vaccine.getProphylaxis().isActive()).isTrue();
        assertThat(vaccine.getAuthHolder().isActive()).isTrue();

        vaccine = result.get(3);
        assertThat(vaccine.isActive()).isFalse();
        assertThat(vaccine.getProphylaxis().isActive()).isFalse();
        assertThat(vaccine.getAuthHolder().isActive()).isFalse();
    }

    @Test
    @Transactional
    void findAll_ok_four_matches_of_four() {
        // given
        persistVaccine("EU/1/20/0005",
                       "Test active 05",
                       true,
                       false,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0006",
                       "Test active 06",
                       true,
                       false,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0007",
                       "Test active 07",
                       true,
                       false,
                       "1119349007",
                       "Prophylaxis active",
                       true,
                       "ORG-100030215",
                       "Test company active",
                       true,
                       200,
                       false,
                       true,
                       false);
        persistVaccine("EU/1/20/0008",
                       "Test active 08",
                       true,
                       false,
                       "1119349007",
                       "Prophylaxis not active",
                       false,
                       "ORG-100030215",
                       "Test company not active",
                       false,
                       200,
                       false,
                       true,
                       false);
        // when
        List<Vaccine> result = vaccineRepository.findAll();
        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(4);

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getProphylaxis().isActive()).isTrue();
        assertThat(vaccine.getAuthHolder().isActive()).isFalse();

        vaccine = result.get(1);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getProphylaxis().isActive()).isFalse();
        assertThat(vaccine.getAuthHolder().isActive()).isTrue();

        vaccine = result.get(2);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getProphylaxis().isActive()).isTrue();
        assertThat(vaccine.getAuthHolder().isActive()).isTrue();

        vaccine = result.get(3);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getProphylaxis().isActive()).isFalse();
        assertThat(vaccine.getAuthHolder().isActive()).isFalse();
    }

    @Test
    @Transactional
    void findAllWebUiActive_ok_in_correct_order() {
        // given
        persistVaccine("Covishield",
                "Covishield (ChAdOx1_nCoV-19)",
                true,
                true,
                "1119349007",
                "SARS-CoV-2 mRNA vaccine",
                true,
                "ORG-100030215",
                "Biontech Manufacturing GmbH",
                true,
                50,
                true,
                false,
                true);
        persistVaccine("EU/1/20/1528",
                "Comirnaty",
                true,
                true,
                "1119349007",
                "SARS-CoV-2 mRNA vaccine",
                true,
                "ORG-100030215",
                "Biontech Manufacturing GmbH",
                true,
                10,
                true,
                false,
                false);
        persistVaccine("EU/1/20/1507",
                "Spikevax (previously COVID-19 Vaccine Moderna)",
                true,
                true,
                "1119349007",
                "SARS-CoV-2 mRNA vaccine",
                true,
                "ORG-100030215",
                "Biontech Manufacturing GmbH",
                true,
                20,
                true,
                false,
                false);

        persistVaccine("EU/1/20/1528",
                "Sputnik-V",
                true,
                false,
                "1119349007",
                "SARS-CoV-2 mRNA vaccine",
                true,
                "ORG-100030215",
                "Biontech Manufacturing GmbH",
                true,
                200,
                false,
                false,
                false);
        // when
        List<Vaccine> result = vaccineRepository.findAllWebUiActive();
        List<Integer> uiOrder = result.stream().map(Vaccine::getWebUiOrder).collect(Collectors.toList());
        boolean isSortedAsc = uiOrder.stream().sorted().collect(Collectors.toList()).equals(uiOrder);

        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(3);
        assertThat(isSortedAsc).isTrue();

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getIssuable()).isEqualTo(Issuable.CH_ONLY);
    }

    private void persistVaccine(
            String code,
            String display,
            boolean active,
            boolean chIssuable,
            String prophylaxisCode,
            String prophylaxisDisplayName,
            boolean prophylaxisActive,
            String authHolderCode,
            String authHolderDisplayName,
            boolean authHolderActive,
            int webUiOrder,
            boolean webUiSelectable,
            boolean apiGatewaySelectable,
            boolean apiPlatformSelectable) {

        Vaccine vaccine = new Vaccine(
                code,
                display,
                active,
                chIssuable,
                Issuable.CH_ONLY,
                webUiOrder,
                webUiSelectable,
                apiGatewaySelectable,
                apiPlatformSelectable);
        Prophylaxis prophylaxis = new Prophylaxis(
                prophylaxisCode,
                prophylaxisDisplayName,
                prophylaxisActive);
        vaccine.setProphylaxis(prophylaxis);
        entityManager.persist(prophylaxis);
        AuthHolder authHolder = new AuthHolder(
                authHolderCode,
                authHolderDisplayName,
                authHolderActive);
        vaccine.setAuthHolder(authHolder);
        entityManager.persist(authHolder);
        entityManager.persist(vaccine);
    }
}
