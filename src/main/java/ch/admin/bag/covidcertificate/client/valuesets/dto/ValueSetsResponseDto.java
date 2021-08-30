package ch.admin.bag.covidcertificate.client.valuesets.dto;

import lombok.*;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ValueSetsResponseDto {

    private String valueSetId;

    private String valueSetDate;

    private Map<String, ValueSetDto> valueSetValues;

}
