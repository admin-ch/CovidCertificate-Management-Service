package ch.admin.bag.covidcertificate.api.valueset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestDto extends IssuableTestDto {
    @Schema(type = "boolean")
    private Boolean active;

    public TestDto(String code, String display, TestType testType, Boolean active) {
        super(code, display, testType);
        this.active = active;
    }
}
