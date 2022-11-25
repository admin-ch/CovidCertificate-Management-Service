package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProphylaxisRepository extends JpaRepository<Prophylaxis, UUID> {
    Prophylaxis findByCode(@Param("code") String code);

    @Query(value = "select distinct p.code from Prophylaxis p ")
    List<String> findAllCodes();
}
