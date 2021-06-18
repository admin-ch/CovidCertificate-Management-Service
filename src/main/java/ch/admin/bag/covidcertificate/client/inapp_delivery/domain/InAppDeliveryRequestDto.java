package ch.admin.bag.covidcertificate.client.inapp_delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class InAppDeliveryRequestDto {
    /**
     * Code of the App the certificate should be sent to. Must be 9 characters, alphanumeric and upper case.
     */
    String code;
    /**
     * Payload of the QRCode. Starts with 'HC1:'
     */
    String hcert;
    /**
     * Base64 encoded String of the PDF.
     */
    String pdf;
}
