package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ValueSetUpdateLogRepository extends JpaRepository<ValueSetUpdateLog, UUID> {

    @Query(value = "select vsul from ValueSetUpdateLog vsul " +
            "where vsul.code = :code ")
    ValueSetUpdateLog findByCode(@Param("code") String code);

    @Query(value = "select distinct vsul.code from ValueSetUpdateLog vsul ")
    List<String> findAllCodes();
}
