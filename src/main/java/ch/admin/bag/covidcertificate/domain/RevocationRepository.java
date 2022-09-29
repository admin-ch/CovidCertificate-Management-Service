package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RevocationRepository extends JpaRepository<Revocation, UUID> {
    Revocation findByUvci(String uvci);

    @Query("SELECT r.uvci FROM Revocation r WHERE r.deletedDateTime IS NULL")
    List<String> findNotDeletedUvcis();

    @Query(value = "SELECT * FROM revocation r WHERE r.fraud = false AND r.deleted_date_time IS NULL AND " +
            "EXISTS (SELECT * FROM kpi k WHERE k.type = 't' AND k.uvci = r.uvci AND k.timestamp < :latestValidDate) " +
            "ORDER BY r.creation_date_time ASC LIMIT :batchSize", nativeQuery = true)
    List<Revocation> findDeletableUvcis(@Param("latestValidDate") LocalDateTime latestValidDate, @Param("batchSize") int batchSize);

}
