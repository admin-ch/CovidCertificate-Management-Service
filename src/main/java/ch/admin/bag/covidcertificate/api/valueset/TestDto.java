package ch.admin.bag.covidcertificate.api.valueset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestDto extends IssuableTestDto {
    @Schema(type = "boolean")
    private Boolean active;

    public TestDto(String code, String display, TestType testType, Boolean active, ZonedDateTime validUntil) {
        super(code, display, testType, validUntil);
        this.active = active;
    }
}
