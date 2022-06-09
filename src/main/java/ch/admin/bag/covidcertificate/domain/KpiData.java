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

    @Column(name = "in_app_delivery_code")
    String inAppDeliveryCode;

    @Column(name = "key_identifier")
    String keyIdentifier;

    @Column(name = "origin_uvci")
    String originUvci;

    @Column(name = "conversion_reason")
    String conversionReason;

    private KpiData(
            LocalDateTime timestamp,
            String type,
            String value,
            String uvci,
            String details,
            String country,
            String systemSource,
            String inAppDeliveryCode,
            String keyIdentifier,
            String originUvci,
            String conversionReason) {

        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
        this.uvci = uvci;
        this.details = details;
        this.country = country;
        this.systemSource = systemSource;
        this.inAppDeliveryCode = inAppDeliveryCode;
        this.keyIdentifier = keyIdentifier;
        this.originUvci = originUvci;
        this.conversionReason = conversionReason;
    }

    public static class KpiDataBuilder {
        LocalDateTime timestamp;
        String type;
        String value;
        String systemSource;
        String uvci;
        String details;
        String country;
        String inAppDeliveryCode;
        String keyIdentifier;
        String originUvci;
        String conversionReason;

        public KpiDataBuilder(LocalDateTime timestamp, String type, String value, String systemSource) {
            this.timestamp = timestamp;
            this.type = type;
            this.value = value;
            this.systemSource = systemSource;
        }

        public KpiDataBuilder withUvci(String uvci) {
            this.uvci = uvci;
            return this;
        }

        public KpiDataBuilder withDetails(String details) {
            this.details = details;
            return this;
        }

        public KpiDataBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public KpiDataBuilder withInAppDeliveryCode(String inAppDeliveryCode) {
            this.inAppDeliveryCode = inAppDeliveryCode;
            return this;
        }

        public KpiDataBuilder withKeyIdentifier(String keyIdentifier) {
            this.keyIdentifier = keyIdentifier;
            return this;
        }

        public KpiDataBuilder withOriginUvci(String originUvci) {
            this.originUvci = originUvci;
            return this;
        }

        public KpiDataBuilder withConversionReason(String conversionReason) {
            this.conversionReason = conversionReason;
            return this;
        }

        public KpiData build() {
            return new KpiData(
                    timestamp,
                    type,
                    value,
                    uvci,
                    details,
                    country,
                    systemSource,
                    inAppDeliveryCode,
                    keyIdentifier,
                    originUvci,
                    conversionReason);
        }
    }
}