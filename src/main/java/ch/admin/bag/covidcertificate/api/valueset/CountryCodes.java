package ch.admin.bag.covidcertificate.api.valueset;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CountryCodes {
    private final List<CountryCode> de;
    private final List<CountryCode> en;
    private final List<CountryCode> fr;
    private final List<CountryCode> it;
}
