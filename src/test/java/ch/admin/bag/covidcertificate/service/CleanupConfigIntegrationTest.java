package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.authorization.ProfileRegistry;
import ch.admin.bag.covidcertificate.config.cleanup.Cleanup;
import ch.admin.bag.covidcertificate.config.cleanup.CleanupConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * This integration test verifies the configured cleanup spots.<p>
 * A cleanup spot defines a database and sql queries used to delete part of the database data.
 * Due to the fact that the queries are in plain text they are not verified until the moment they
 * get sent to the database. <br>
 * This test is verifying the queries by establishing a h2 database and using the queries on that database.
 */
@Slf4j
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testDB;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true",
        "cc-management-service.clean.cron=\"-\"",
        // management-db
        "cc-management-service.cleanup.spots.management-db.database.driver-class-name=org.h2.Driver",
        "cc-management-service.cleanup.spots.management-db.database.url=jdbc:h2:mem:testDB;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "cc-management-service.cleanup.spots.management-db.database.username=sa",
        "cc-management-service.cleanup.spots.management-db.database.password=sa",
        // printing-db
        "cc-management-service.cleanup.spots.printing-db.database.driver-class-name=org.h2.Driver",
        "cc-management-service.cleanup.spots.printing-db.database.url=jdbc:h2:mem:testDB;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "cc-management-service.cleanup.spots.printing-db.database.username=sa",
        "cc-management-service.cleanup.spots.printing-db.database.password=sa",
        // lightcer-generation-db
        "cc-management-service.cleanup.spots.lightcer-generation-db.database.driver-class-name=org.h2.Driver",
        "cc-management-service.cleanup.spots.lightcer-generation-db.database.url=jdbc:h2:mem:testDB;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "cc-management-service.cleanup.spots.lightcer-generation-db.database.username=sa",
        "cc-management-service.cleanup.spots.lightcer-generation-db.database.password=sa",
        // api-gateway-db
        "cc-management-service.cleanup.spots.api-gateway-db.database.driver-class-name=org.h2.Driver",
        "cc-management-service.cleanup.spots.api-gateway-db.database.url=jdbc:h2:mem:testDB;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "cc-management-service.cleanup.spots.api-gateway-db.database.username=sa",
        "cc-management-service.cleanup.spots.api-gateway-db.database.password=sa"
})
@ActiveProfiles({"local", "h2", ProfileRegistry.AUTHORIZATION_MOCK})
@MockBean(InMemoryClientRegistrationRepository.class)
class CleanupConfigIntegrationTest {

    @Autowired
    private CleanupConfig cleanupConfig;

    @Autowired
    private CleanupService cleanupService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    private void setup() {
        log.info("SETUP HERE");
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS public.billing_kpi (" +
                                    "id uuid NOT NULL, " +
                                    "canton_code_sender character varying(2), " +
                                    "uvci character varying(39) NOT NULL, " +
                                    "processed_at timestamp without time zone, " +
                                    "is_billable boolean, " +
                                    "CONSTRAINT billing_kpi_pkey PRIMARY KEY (id) )");
    }

    @Test
    void expectFourSpots() {
        Map<String, Cleanup> spots = cleanupConfig.getSpots();
        assertEquals(4, spots.size());
        List<String> names = new ArrayList<>(4);
        CollectionUtils.addAll(names, spots.keySet());
        Collections.sort(names);
        assertEquals("[api-gateway-db, lightcer-generation-db, management-db, printing-db]", names.toString());
    }

    @Test
    void expectValidConfiguration() {
        Map<String, Cleanup> spots = cleanupConfig.getSpots();
        for (String spotName : spots.keySet()) {
            Cleanup cleanup = spots.get(spotName);
            assertNotNull(cleanup, spotName);

            CleanupService.CleaningEffort effort = cleanupService.create(spotName, cleanup);
            assertNotNull(effort, spotName + ", " +cleanup.getDatabase());

            try {
                long count = cleanupService.getCount(effort, LocalDate.now());
                assertEquals(0L, count);
            } catch (Throwable t) {
                fail("exception at count-query with "+spotName);
            }

            try {
                long count = cleanupService.delete(effort, LocalDate.now(), false);
                assertEquals(0L, count);
            } catch (Throwable t) {
                fail("exception at delete-until-batch-query with "+spotName);
            }

            try {
                long count = cleanupService.delete(effort, LocalDate.now(), true);
                assertEquals(0L, count);
            } catch (Throwable t) {
                fail("exception at delete-until-query with "+spotName);
            }
        }
    }
}