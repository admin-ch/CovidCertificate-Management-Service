package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthHolderRepository extends JpaRepository<AuthHolder, UUID> {
    AuthHolder findByCode(@Param("code") String code);

    @Query(value = "select distinct ah.code from AuthHolder ah ")
    List<String> findAllCodes();
}
