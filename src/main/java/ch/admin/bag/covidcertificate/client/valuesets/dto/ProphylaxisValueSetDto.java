package ch.admin.bag.covidcertificate.client.valuesets.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProphylaxisValueSetDto {
    private String display;

    private String lang;

    private boolean active;

    private String system;

    private String version;
}
