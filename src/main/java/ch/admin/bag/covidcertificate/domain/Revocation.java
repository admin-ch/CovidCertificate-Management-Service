package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "revocation")
public class Revocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    String uvci;
    boolean fraud;
    @Column(insertable = false)
    LocalDateTime creationDateTime;

    public Revocation(String uvci, boolean fraud) {
        this.uvci = uvci;
        this.fraud = fraud;
    }
}
