package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.domain.BiData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BiDataDto {
    UUID id;
    LocalDateTime timestamp;
    String type;
    String value;
    String details;
    String country;
    String systemSource;
    String apiGatewayId;
    String inAppDeliveryCode;
    Boolean fraud;
    String keyIdentifier;
}
