package ch.admin.bag.covidcertificate.client.valuesets.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProphylaxisValueSetResponseDto {

    private String valueSetId;

    private String valueSetDate;

    private Map<String, ProphylaxisValueSetDto> valueSetValues;

}
