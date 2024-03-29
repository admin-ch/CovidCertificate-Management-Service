package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VaccineRepository extends JpaRepository<Vaccine, UUID> {

    @Query(value = "select v from Vaccine v " +
            "join AuthHolder a on a.id = v.authHolder.id " +
            "join Prophylaxis p on p.id = v.prophylaxis.id " +
            "order by v.display asc")
    List<Vaccine> findAllValid();

    @Query(value = "select v from Vaccine v " +
            "join AuthHolder a on a.id = v.authHolder.id " +
            "join Prophylaxis p on p.id = v.prophylaxis.id " +
            "where v.active = true " +
            "and a.active = true " +
            "and p.active = true " +
            "and v.apiGatewaySelectable = true " +
            "order by v.display asc")
    List<Vaccine> findAllGatewayApiActive();

    @Query(value = "select v from Vaccine v " +
            "join AuthHolder a on a.id = v.authHolder.id " +
            "join Prophylaxis p on p.id = v.prophylaxis.id " +
            "where v.active = true " +
            "and v.webUiSelectable = true " +
            "order by v.vaccineOrder asc")
    List<Vaccine> findAllWebUiActive();

    @Query(value = "select v from Vaccine v " +
            "join AuthHolder a on a.id = v.authHolder.id " +
            "join Prophylaxis p on p.id = v.prophylaxis.id " +
            "where v.active = true " +
            "and v.apiPlatformSelectable = true " +
            "order by v.display asc")
    List<Vaccine> findAllPlatformApiActive();

    List<Vaccine> findByCode(@Param("code") String code);

    @Query(value = "select distinct v.code from Vaccine v ")
    List<String> findAllCodes();
}
