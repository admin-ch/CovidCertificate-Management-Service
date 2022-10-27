package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisplayNameModificationRepository extends JpaRepository<DisplayNameModification, String> {

    @Query(value = "select dnm from DisplayNameModification dnm " +
            "where dnm.code = :code " +
            "and dnm.entityType = :entityType ")
    Optional<DisplayNameModification> findByCodeAndEntityType(@Param("code") String code, @Param("entityType") EntityType entityType);
}
