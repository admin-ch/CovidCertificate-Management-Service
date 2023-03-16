package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.config.cleanup.Cleanup;
import ch.admin.bag.covidcertificate.config.cleanup.Database;
import ch.admin.bag.covidcertificate.config.cleanup.SqlQuery;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    /**
     * Deletes the next portion of records limited by given date.<br>
     * The number of records to delete may be limited to batch-size by the <code>asBatch</code> flag.
     *
     * @param effort  the database to delete a batch from
     * @param date    records older than this date are delete-candidates
     * @param asBatch <code>true</code> demands deletion of configured batch-size,
     *                <code>false</code> deletes everything
     * @return number of records this batch deleted
     */
    @Transactional
    public long delete(CleaningEffort effort, LocalDate date, boolean asBatch) {
        long count = -1L;
        if (effort != null) {
            if (asBatch) {
                count = effort.jdbcTemplate.update(effort.deleteUntilBatchQuery, new Object[] { date, effort.deleteUntilBatchSize});
            } else {
                count = effort.jdbcTemplate.update(effort.deleteUntilQuery, new Object[] { date });
            }
        }
        return count;
    }

    public long getCount(CleaningEffort effort, LocalDate date) {
        long count = -1L;
        if (effort != null) {
            count = effort.jdbcTemplate.queryForObject(effort.countQuery, Long.class, new Object[] { date } );
        }
        return count;
    }

    public CleaningEffort create(String databaseName, Cleanup cleanup) {

        CleaningEffort effort = null;

        boolean nameValid = StringUtils.isNoneBlank(databaseName);
        if (!nameValid) {
            log.debug("CLEANING | Init {} - name '{}' invalid", databaseName, databaseName);
        }

        Database db = cleanup.getDatabase();
        boolean databaseValid = false;
        if (db == null) {
            log.debug("CLEANING | Init {} - database config for '{}' missing", databaseName, databaseName);
        } else {
            databaseValid = StringUtils.isNoneBlank(db.getUrl(), db.getUsername(), db.getPassword());
            if (!databaseValid) {
                log.debug("CLEANING | Init {} - database config invalid:\n url='{}'\n username='{}'\n password='***", databaseName, db.getUrl(), db.getUsername());
            }
        }

        SqlQuery query = cleanup.getSqlQuery();
        boolean sqlQueryValid = false;
        if (query == null) {
            log.debug("CLEANING | Init {} - query config for '{}' missing", databaseName, databaseName);
        } else {
            sqlQueryValid = StringUtils.isNoneBlank(
                    query.getCount(), query.getDeleteUntil(), query.getDeleteUntilBatch()) && query.getDeleteUntilBatchSize()>-1;
            if (!sqlQueryValid) {
                log.debug("CLEANING | Init {} - query config invalid:\n count='{}'\n deleteAll='{}'\n deleteBatch='{}'\ndeleteBatchSize={}", databaseName,
                        query.getCount(), query.getDeleteUntil(), query.getDeleteUntilBatch(), query.getDeleteUntilBatchSize());
            }
        }

        if (nameValid && databaseValid && sqlQueryValid) {
            HikariDataSource hikariDataSource = null;
            try {
                hikariDataSource = createHikariDataSource(databaseName, db.getUrl(), db.getUsername(), db.getPassword());
                JdbcTemplate jdbcTemplate = createJdbcTemplate(hikariDataSource);
                effort = new CleaningEffort(databaseName, query.getCount(), query.getDeleteUntil(),
                        query.getDeleteUntilBatch(), query.getDeleteUntilBatchSize(), hikariDataSource, jdbcTemplate);
            } catch (Exception e) {
                log.debug("CLEANING | Init {} - error: '{}'", databaseName, e.getMessage());
                if (hikariDataSource != null) {
                    try {
                        hikariDataSource.close();
                    } catch (Throwable t) {
                        log.debug("CLEANING | Init {} - error on closing datasource: '{}'", databaseName, t.getMessage());
                    }
                }
            }
        }

        return effort;
    }

    public void destroy(CleaningEffort effort) {
        JdbcTemplate jdbcTemplate = effort.getJdbcTemplate();
        effort.setJdbcTemplate(null);

        jdbcTemplate.setDataSource(null);

        HikariDataSource hikariDataSource = effort.getDataSource();
        effort.setDataSource(null);

        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    private HikariDataSource createHikariDataSource(String databaseName, String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaximumPoolSize(2);
        dataSource.setMinimumIdle(0);
        dataSource.setPoolName("cleanup-"+databaseName);
        return dataSource;
    }

    private JdbcTemplate createJdbcTemplate(HikariDataSource hikariDataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);
        jdbcTemplate.queryForObject("select true where 1=1", Boolean.class);
        return jdbcTemplate;
    }

    @Data
    @AllArgsConstructor
    class CleaningEffort {

        private final String name;
        private final String countQuery;
        private final String deleteUntilQuery;
        private final String deleteUntilBatchQuery;
        private final int deleteUntilBatchSize;
        private HikariDataSource dataSource;
        private JdbcTemplate jdbcTemplate;
    }
}
