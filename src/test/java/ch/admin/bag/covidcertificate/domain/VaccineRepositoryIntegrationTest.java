package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.api.request.Issuable;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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
        PersistableVaccineBuilder.usingDefaults().apiGateway(true).persist(entityManager);
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
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0001")
                .active(false)
                .prophylaxisActive(true)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0002")
                .active(false)
                .prophylaxisActive(false)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0003")
                .active(false)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0004")
                .active(false)
                .prophylaxisActive(false)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);

        // when
        List<Vaccine> result = vaccineRepository.findAllGatewayApiActive();
        // then
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @Transactional
    void findAllActiveAndChIssuable_ok_one_match_of_four() {
        // given
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0005")
                .active(true)
                .prophylaxisActive(true)
                .authHolderActive(false)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0006")
                .active(true)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0007")
                .active(true)
                .prophylaxisActive(true)
                .authHolderActive(false)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0008")
                .active(true)
                .prophylaxisActive(false)
                .authHolderActive(false)
                .persist(entityManager);
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
        PersistableVaccineBuilder.usingDefaults()
                .chIssuable(false)
                .authHolderActive(false, "Biontech Manufacturing GmbH")
                .apiGateway(true)
                .persist(entityManager);
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
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0001")
                .active(false)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0002")
                .active(false)
                .chIssuable(false)
                .prophylaxisActive(false)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0003")
                .active(false)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0004")
                .active(false)
                .chIssuable(false)
                .prophylaxisActive(false)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);
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
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0005")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0006")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(false)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0007")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .apiGateway(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0008")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(false)
                .authHolderActive(false)
                .apiGateway(true)
                .persist(entityManager);
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
        PersistableVaccineBuilder.usingDefaults()
                .code("Covishield")
                .active(true, "Covishield (ChAdOx1_nCoV-19)")
                .chIssuable(true)
                .prophylaxisActive(true, "SARS-CoV-2 mRNA vaccine")
                .authHolderActive(true, "Biontech Manufacturing GmbH")
                .order(50)
                .webUI(true)
                .apiPlatform(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/1528")
                .active(true, "Comirnaty")
                .chIssuable(true)
                .prophylaxisActive(true, "SARS-CoV-2 mRNA vaccine")
                .authHolderActive(true, "Biontech Manufacturing GmbH")
                .order(10)
                .webUI(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/1507")
                .active(true, "Spikevax (previously COVID-19 Vaccine Moderna)")
                .chIssuable(true)
                .prophylaxisActive(true, "SARS-CoV-2 mRNA vaccine")
                .authHolderActive(true, "Biontech Manufacturing GmbH")
                .order(20)
                .webUI(true)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/1528")
                .active(true, "Sputnik-V")
                .chIssuable(false)
                .prophylaxisActive(true, "SARS-CoV-2 mRNA vaccine")
                .authHolderActive(true, "Biontech Manufacturing GmbH")
                .order(200)
                .persist(entityManager);

        // when
        List<Vaccine> result = vaccineRepository.findAllWebUiActive();
        List<Integer> uiOrder = result.stream().map(Vaccine::getVaccineOrder).collect(Collectors.toList());
        boolean isSortedAsc = uiOrder.stream().sorted().collect(Collectors.toList()).equals(uiOrder);

        // then
        assertThat(result).isNotNull().isNotEmpty().hasSize(3);
        assertThat(isSortedAsc).isTrue();

        Vaccine vaccine = result.get(0);
        assertThat(vaccine.isActive()).isTrue();
        assertThat(vaccine.getIssuable()).isEqualTo(Issuable.CH_ONLY);
    }

    @Test
    @Transactional
    void findAll_within_validity_range() {
        // given
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0010")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .webUI(true)
                .apiGateway(true)
                .apiPlatform(true)
                .validFromTo(PersistableVaccineBuilder.YESTERDAY, PersistableVaccineBuilder.TOMORROW)
                .persist(entityManager);
        PersistableVaccineBuilder.usingDefaults()
                .code("EU/1/20/0011")
                .active(true)
                .chIssuable(false)
                .prophylaxisActive(true)
                .authHolderActive(true)
                .webUI(true)
                .apiGateway(true)
                .apiPlatform(true)
                .validFromTo(PersistableVaccineBuilder.TOMORROW, PersistableVaccineBuilder.TOMORROW)
                .persist(entityManager);
        // when
        List<Vaccine> apiGatewayResult = vaccineRepository.findAllGatewayApiActive();
        // then
        assertThat(apiGatewayResult).isNotNull().isNotEmpty().hasSize(1);

        Vaccine apiGatewayVaccine = apiGatewayResult.get(0);
        assertThat(apiGatewayVaccine.getCode()).isEqualTo("EU/1/20/0010");
        assertThat(apiGatewayVaccine.isActive()).isTrue();
        assertThat(apiGatewayVaccine.getProphylaxis().isActive()).isTrue();
        assertThat(apiGatewayVaccine.getAuthHolder().isActive()).isTrue();

        // when
        List<Vaccine> webUiResult = vaccineRepository.findAllWebUiActive();
        // then
        assertThat(webUiResult).isNotNull().isNotEmpty().hasSize(1);

        Vaccine webUiVaccine = webUiResult.get(0);
        assertThat(webUiVaccine.getCode()).isEqualTo("EU/1/20/0010");
        assertThat(webUiVaccine.isActive()).isTrue();
        assertThat(webUiVaccine.getProphylaxis().isActive()).isTrue();
        assertThat(webUiVaccine.getAuthHolder().isActive()).isTrue();

        // when
        List<Vaccine> apiPlatformResult = vaccineRepository.findAllPlatformApiActive();
        // then
        assertThat(apiPlatformResult).isNotNull().isNotEmpty().hasSize(1);

        Vaccine apiPlatformVaccine = apiPlatformResult.get(0);
        assertThat(apiPlatformVaccine.getCode()).isEqualTo("EU/1/20/0010");
        assertThat(apiPlatformVaccine.isActive()).isTrue();
        assertThat(apiPlatformVaccine.getProphylaxis().isActive()).isTrue();
        assertThat(apiPlatformVaccine.getAuthHolder().isActive()).isTrue();
    }
}
