package ch.admin.bag.covidcertificate.api.valueset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IssuableTestDto {
    @Schema(type = "string")
    private String code;
    @Schema(type = "string")
    private String display;
    @JsonIgnore
    private TestType testType;

    ZonedDateTime validUntil;
}
