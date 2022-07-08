package ch.admin.bag.covidcertificate.client.signing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class SigningInformationDto {
    private final String certificateType;
    private final String code;
    private final String alias;
    private final String certificateAlias;
    private final Integer slotNumber;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    @Setter
    private String calculatedKeyIdentifier;
}
