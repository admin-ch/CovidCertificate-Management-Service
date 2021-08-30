package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RapidTestRepository extends JpaRepository<RapidTest, UUID> {

    Optional<RapidTest> findByCode(String code);

    List<RapidTest> findAllByActiveAndModifiedAtIsNot(boolean active, LocalDateTime modifiedAt);

}
