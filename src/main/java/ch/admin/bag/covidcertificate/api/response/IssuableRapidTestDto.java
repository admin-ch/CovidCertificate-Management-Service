package ch.admin.bag.covidcertificate.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IssuableRapidTestDto {
    @Schema(type = "string")
    private String code;
    @Schema(type = "string")
    private String display;
}
