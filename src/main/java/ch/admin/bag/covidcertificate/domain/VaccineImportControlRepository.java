package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VaccineImportControlRepository extends JpaRepository<VaccineImportControl, String> {

    Optional<VaccineImportControl> findByImportDateLessThanEqualAndDoneFalse(@Param("importDate") LocalDate importDate);
}
