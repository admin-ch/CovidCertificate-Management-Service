package ch.admin.bag.covidcertificate.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
}
