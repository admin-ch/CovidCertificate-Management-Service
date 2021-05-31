package ch.admin.bag.covidcertificate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RevocationRepository extends JpaRepository<Revocation, UUID> {
    Revocation findByUvci(String uvci);

    @Query("SELECT r.uvci FROM Revocation r")
    List<String> findAllUvcis();
}
