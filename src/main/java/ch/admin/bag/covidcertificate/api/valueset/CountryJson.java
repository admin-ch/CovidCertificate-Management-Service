package ch.admin.bag.covidcertificate.api.valueset;

import lombok.Getter;

import java.util.List;

@Getter
public class CountryJson {
    private String valueSetId;
    private String valueSetDate;
    private List<CountryCode> valueSetValues;
}
