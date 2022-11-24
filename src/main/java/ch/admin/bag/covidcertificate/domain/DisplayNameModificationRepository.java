package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisplayNameModificationRepository extends JpaRepository<DisplayNameModification, String> {

    Optional<DisplayNameModification> findByCodeAndEntityType(@Param("code") String code, @Param("entityType") EntityType entityType);
}
