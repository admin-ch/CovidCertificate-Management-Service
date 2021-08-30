package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@ActiveProfiles("local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RapidTestImportServiceITTest {

    @Autowired
    private RapidTestImportService service;

    @Autowired
    private RapidTestRepository repository;

    @Test
    void importRapidTests() {
        Assertions.assertThat(repository.findAll().isEmpty()).isTrue();
        service.importRapidTests();
        Assertions.assertThat(repository.findAll().size()).isEqualTo(140);
    }
}
