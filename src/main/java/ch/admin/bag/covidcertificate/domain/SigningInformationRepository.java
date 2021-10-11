package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SigningInformationRepository extends JpaRepository<SigningInformation, UUID> {

    @Query(value = "select s from SigningInformation s " +
            "where s.certificateType = :certificateType " +
            "and s.code = :code ")
    SigningInformation findSigningInformation(
            @Param("certificateType") String certificateType,
            @Param("code") String code);

    @Query(value = "select s from SigningInformation s " +
            "where s.certificateType = :certificateType ")
    List<SigningInformation> findSigningInformation(
            @Param("certificateType") String certificateType);
}
