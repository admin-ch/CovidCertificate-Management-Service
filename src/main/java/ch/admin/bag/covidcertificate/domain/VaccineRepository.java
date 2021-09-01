package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, UUID> {

    @Query(value = "select v from Vaccine v " +
            "where v.active = true " +
            "and v.authHolderActive = true " +
            "and v.prophylaxisActive = true " +
            "and v.chIssuable = true " +
            "order by v.display asc")
    List<Vaccine> findAllActiveAndChIssuable();
}
