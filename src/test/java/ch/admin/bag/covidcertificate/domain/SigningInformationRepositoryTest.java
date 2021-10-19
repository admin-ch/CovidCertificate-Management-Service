package ch.admin.bag.covidcertificate.domain;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
class SigningInformationRepositoryTest {
    @Autowired
    private SigningInformationRepository signingInformationRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final JFixture fixture = new JFixture();

    private static Stream<Arguments> getValidIntervals() {
        return Stream.of(
                Arguments.of(LocalDate.now().minusYears(1), LocalDate.now().plusYears(1)),
                Arguments.of(LocalDate.now().minusYears(1), LocalDate.now()),
                Arguments.of(LocalDate.now(), LocalDate.now().plusYears(1))
        );
    }

    private static Stream<Arguments> getInvalidIntervals() {
        return Stream.of(
                Arguments.of(LocalDate.now().minusYears(1), LocalDate.now().minusDays(1)),
                Arguments.of(LocalDate.now().plusDays(1), LocalDate.now().plusYears(1))
        );
    }

    @Nested
    class FindSigningInformation_ByCodeAndType{
        @Transactional
        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.domain.SigningInformationRepositoryTest#getValidIntervals")
        void shouldReturnMatchingEntry_whenValidAtGivenTimestamp(LocalDate validFrom, LocalDate validTo){
            var signingInformation = fixture.create(SigningInformation.class);
            ReflectionTestUtils.setField(signingInformation, "validFrom", validFrom);
            ReflectionTestUtils.setField(signingInformation, "validTo", validTo);
            var signingInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            entityManager.persist(signingInformation);
            signingInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(signingInformation.getCertificateType(), signingInformation.getCode(), LocalDate.now());

            assertEquals(signingInformation, actual);
        }

        @Transactional
        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.domain.SigningInformationRepositoryTest#getInvalidIntervals")
        void shouldNotReturnMatchingEntry_whenInvalidAtGivenTimestamp(LocalDate validFrom, LocalDate validTo){
            var signingInformation = fixture.create(SigningInformation.class);
            ReflectionTestUtils.setField(signingInformation, "validFrom", validFrom);
            ReflectionTestUtils.setField(signingInformation, "validTo", validTo);
            var signingInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            entityManager.persist(signingInformation);
            signingInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(signingInformation.getCertificateType(), signingInformation.getCode(), LocalDate.now());

            assertNull(actual);
        }

    }

    @Nested
    class FindSigningInformation_ByType{
        @Transactional
        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.domain.SigningInformationRepositoryTest#getValidIntervals")
        void shouldReturnMatchingEntries_whenValidAtGivenTimestamp(LocalDate validFrom, LocalDate validTo){
            var type = fixture.create(String.class);
            var matchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach(entry -> {
                        ReflectionTestUtils.setField(entry, "certificateType", type);
                        ReflectionTestUtils.setField(entry, "validFrom", validFrom);
                        ReflectionTestUtils.setField(entry, "validTo", validTo);
                    }
            );
            var nonMatchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach( it -> entityManager.persist(it));
            nonMatchingSigningInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(type, LocalDate.now());

            assertEquals(matchingSigningInformationEntries, actual);
        }

        @Transactional
        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.domain.SigningInformationRepositoryTest#getInvalidIntervals")
        void shouldNotReturnMatchingEntries_whenInvalidAtGivenTimestamp(LocalDate validFrom, LocalDate validTo){
            var type = fixture.create(String.class);
            var matchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach(entry -> {
                        ReflectionTestUtils.setField(entry, "certificateType", type);
                        ReflectionTestUtils.setField(entry, "validFrom", validFrom);
                        ReflectionTestUtils.setField(entry, "validTo", validTo);
                    }
            );
            var nonMatchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach( it -> entityManager.persist(it));
            nonMatchingSigningInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(type, LocalDate.now());

            assertThat(actual.isEmpty());
        }
    }
}