package ch.admin.bag.covidcertificate.domain;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Nested
    class FindSigningInformation_ByCodeAndType{
        @Transactional
        @Test
        void shouldReturnMatchingEntry(){
            var signingInformation = fixture.create(SigningInformation.class);
            var signingInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            entityManager.persist(signingInformation);
            signingInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(signingInformation.getCertificateType(), signingInformation.getCode());

            assertEquals(signingInformation, actual);
        }
    }

    @Nested
    class FindSigningInformation_ByType{
        @Transactional
        @Test
        void shouldReturnMatchingEntries(){
            var type = fixture.create(String.class);
            var matchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach(entry -> ReflectionTestUtils.setField(entry, "certificateType", type));
            var nonMatchingSigningInformationEntries = fixture.collections().createCollection(SigningInformation.class);
            matchingSigningInformationEntries.forEach( it -> entityManager.persist(it));
            nonMatchingSigningInformationEntries.forEach( it -> entityManager.persist(it));

            var actual = signingInformationRepository.findSigningInformation(type);

            assertEquals(matchingSigningInformationEntries, actual);
        }
    }
}