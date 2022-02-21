package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
    String apiGatewayId;
    boolean fraud;
    @Column(name = "in_app_delivery_code")
    String inAppDeliveryCode;

    public KpiData(LocalDateTime timestamp, String type, String value) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.systemSource = "UI";
    }

    public KpiData(LocalDateTime timestamp, String type, String value, String uvci, String details, String country, boolean fraud) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.uvci = uvci;
        this.details = details;
        this.country = country;
        this.fraud = fraud;
        this.systemSource = "UI";
    }
    public KpiData(
            LocalDateTime timestamp, String type, String value, String uvci,
            String details, String country) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.uvci = uvci;
        this.details = details;
        this.country = country;
        this.systemSource = "UI";
    }

    public KpiData(
            LocalDateTime timestamp, String type, String value, String uvci,
            String details, String country, String inAppDeliveryCode) {
        this.timestamp = timestamp;
        this.value = value;
        this.type = type;
        this.uvci = uvci;
        this.details = details;
        this.country = country;
        this.systemSource = "UI";
        this.inAppDeliveryCode = inAppDeliveryCode;
    }
}