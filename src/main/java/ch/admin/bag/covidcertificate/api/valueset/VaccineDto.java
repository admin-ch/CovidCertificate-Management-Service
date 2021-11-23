package ch.admin.bag.covidcertificate.api.valueset;

import ch.admin.bag.covidcertificate.api.request.Issuable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccineDto extends IssuableVaccineDto {
    @Schema(type = "boolean")
    private Boolean active;

    public VaccineDto(
            String productCode, String productDisplay, String prophylaxisCode, String prophylaxisDisplay,
            String authHolderCode, String authHolderDisplay, Boolean active, Issuable issuable,
            boolean touristVaccine) {
        super(productCode, productDisplay, prophylaxisCode, prophylaxisDisplay,
              authHolderCode, authHolderDisplay, issuable, touristVaccine);
        this.active = active;
    }
}
