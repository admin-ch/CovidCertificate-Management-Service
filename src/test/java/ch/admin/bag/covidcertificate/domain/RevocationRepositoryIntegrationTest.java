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

import static org.junit.jupiter.api.Assertions.*;

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
class RevocationRepositoryIntegrationTest {
    @Autowired
    private RevocationRepository revocationRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void givenNoRevocationInDB_whenFindByUvci_thenReturnNull() {
        // given when
        Revocation result = revocationRepository.findByUvci("urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E");
        // then
        assertNull(result);
    }

    @Test
    @Transactional
    void givenRevocationInDB_whenFindByUvci_thenReturnRevocation() {
        // given
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        persistRevocation(uvci);
        // when
        Revocation result = revocationRepository.findByUvci(uvci);
        // then
        assertEquals(uvci, result.getUvci());
    }

    @Test
    @Transactional
    void givenNoRevocationInDB_whenFindAllUvcis_thenReturnEmptyList() {
        // given when
        List<String> result = revocationRepository.findAllUvcis();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void givenRevocationsInDB_whenFindAllUvcis_thenReturnRevocations() {
        // given
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        persistRevocation(uvci);
        persistRevocation("urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53F");
        // when
        List<String> result = revocationRepository.findAllUvcis();
        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(uvci));
    }

    private void persistRevocation(String uvci) {
        Revocation revocation = new Revocation(uvci, false);
        entityManager.persist(revocation);
    }
}
