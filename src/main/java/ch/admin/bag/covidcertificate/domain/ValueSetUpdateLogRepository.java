package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ValueSetUpdateLogRepository extends JpaRepository<ValueSetUpdateLog, UUID> {
}