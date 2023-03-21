package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.config.cleanup.Cleanup;
import ch.admin.bag.covidcertificate.config.cleanup.Database;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CleanupServiceTest {
    @InjectMocks
    private CleanupService cleanupService;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private HikariDataSource hikariDataSource;


    @Test
    void createReturnsNull_withoutDatabaseName() {
        CleanupService.CleaningEffort effort = cleanupService.create(null, new Cleanup());
        assertNull(effort);
    }

    @Test
    void createReturnsNull_withoutDatabaseConfig() {
        CleanupService.CleaningEffort effort = cleanupService.create("name", new Cleanup());
        assertNull(effort);
    }

    @Test
    void createReturnsNull_withoutSqlQueryConfig() {
        Cleanup cleanup = new Cleanup();
        cleanup.setDatabase(new Database());
        CleanupService.CleaningEffort effort = cleanupService.create("name", cleanup);
        assertNull(effort);
    }

    @Test
    void destroy_successful() {

        doNothing().when(jdbcTemplate).setDataSource(null);
        doNothing().when(hikariDataSource).close();

        CleanupService.CleaningEffort effort = new CleanupService.CleaningEffort("name",
                "countQuery", "deleteUntilQuery", "deleteUntilBatchQuery", 5,
                hikariDataSource, jdbcTemplate);

        cleanupService.destroy(effort);

        verify(jdbcTemplate).setDataSource(null);
        verify(hikariDataSource).close();

        assertNull(effort.getDataSource());
        assertNull(effort.getJdbcTemplate());
    }
}