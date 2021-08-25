package ch.admin.bag.covidcertificate.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RapidTestDto extends IssuableRapidTestDto {
    @Schema(type = "boolean")
    private Boolean active;

    public RapidTestDto(String code, String display, Boolean active) {
        super(code, display);
        this.active = active;
    }
}
