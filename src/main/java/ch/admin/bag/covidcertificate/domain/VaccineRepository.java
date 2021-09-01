package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, UUID> {

    @Query(value = "select v from Vaccine v " +
            "join AuthHolder a on a.id = v.authHolder.id " +
            "join Prophylaxis p on p.id = v.prophylaxis.id " +
            "where v.active = true " +
            "and a.active = true " +
            "and p.active = true " +
            "and v.chIssuable = true " +
            "order by v.display asc")
    List<Vaccine> findAllActiveAndChIssuable();
}
