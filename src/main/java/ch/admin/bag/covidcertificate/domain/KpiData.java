package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "kpi")
public class KpiData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    LocalDateTime timestamp;
    String type;
    String value;
    String uvci;
    String details;
    String country;
    String systemSource;
    String originalId;

    public KpiData(LocalDateTime timestamp, String type, String value, String uvci, String details, String country) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.uvci = uvci;
        this.details = details;
        this.country = country;
        this.systemSource = "UI";
    }

    public KpiData(LocalDateTime timestamp, String type, String value) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.systemSource = "UI";
    }
}