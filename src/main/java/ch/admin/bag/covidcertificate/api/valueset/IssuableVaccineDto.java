package ch.admin.bag.covidcertificate.api.valueset;

import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
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

    public IssuableVaccineDto(String productCode, String productDisplay) {
        this.productCode = productCode;
        this.productDisplay = productDisplay;
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
