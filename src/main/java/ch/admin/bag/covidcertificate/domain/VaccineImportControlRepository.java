package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VaccineImportControlRepository extends JpaRepository<VaccineImportControl, String> {

    @Query(value = "select vic from VaccineImportControl vic " +
            "where vic.importDate <= :importDate " +
            "and vic.done = false")
    Optional<VaccineImportControl> findByImportDate(@Param("importDate") LocalDate importDate);
}
