package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface KpiDataRepository extends JpaRepository<KpiData, UUID> {
    KpiData findByUvci(String uvci);

    @Query(value = "select k.id as id" +
            ", k.timestamp as timestamp" +
            ", k.value as value" +
            ", k.details as details" +
            ", k.country as country" +
            ", k.systemSource as systemSource" +
            ", k.apiGatewayId as apiGatewayId" +
            ", k.inAppDeliveryCode as inAppDeliveryCode" +
            ", r.fraud as fraud" +
            ", k.keyIdentifier as keyIdentifier " +
            "from KpiData k " +
            "LEFT JOIN Revocation r on r.uvci = k.uvci " +
            "WHERE k.timestamp BETWEEN :fromDateTime AND :toDateTime " +
            "ORDER BY k.timestamp asc")
    List<BiData> findAllByDateRange(@Param("fromDateTime") LocalDateTime fromDateTime, @Param("toDateTime") LocalDateTime toDateTime);
}
