package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    @Setter
    @Column(name = "deleted_date_time")
    LocalDateTime deletedDateTime;
    @Column(insertable = false)
    LocalDateTime creationDateTime;

    public Revocation(String uvci, boolean fraud, LocalDateTime deletedDateTime) {
        this.uvci = uvci;
        this.fraud = fraud;
        this.deletedDateTime = deletedDateTime;
    }
}
