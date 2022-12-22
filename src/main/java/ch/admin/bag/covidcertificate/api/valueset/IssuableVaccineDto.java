package ch.admin.bag.covidcertificate.api.valueset;

import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IssuableVaccineDto {

    @Schema(type = "string")
    private String productCode;
    @Schema(type = "string")
    private String productDisplay;
    @Schema(type = "string")
    private String prophylaxisCode;
    @Schema(type = "string")
    private String prophylaxisDisplay;
    @Schema(type = "string")
    private String authHolderCode;
    @Schema(type = "string")
    private String authHolderDisplay;
    @Setter
    private Issuable issuable;

    @Schema(type = "boolean")
    private boolean touristVaccine;

    public IssuableVaccineDto(String productCode, String productDisplay, Issuable issuable, boolean touristVaccine) {
        this.productCode = productCode;
        this.productDisplay = productDisplay;
        this.issuable = issuable;
        this.touristVaccine = touristVaccine;
    }

    public void addProphylaxisInfo(Prophylaxis prophylaxis) {
        this.prophylaxisCode = prophylaxis.getCode();
        this.prophylaxisDisplay = prophylaxis.getDisplay();
    }

    public void addAuthHolderInfo(AuthHolder authHolder) {
        this.authHolderCode = authHolder.getCode();
        this.authHolderDisplay = authHolder.getDisplay();
    }
}
