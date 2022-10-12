package ch.admin.bag.covidcertificate.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BiData {
    UUID getId();
    LocalDateTime getTimestamp();
    String getType();
    String getValue();
    String getDetails();
    String getCountry();
    String getSystemSource();
    String getApiGatewayId();
    String getInAppDeliveryCode();
    Boolean getFraud();
    String getKeyIdentifier();
}
