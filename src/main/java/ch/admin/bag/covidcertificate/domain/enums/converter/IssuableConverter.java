package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.api.request.Issuable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class IssuableConverter implements AttributeConverter<Issuable, String> {

    @Override
    public String convertToDatabaseColumn(Issuable issuable) {
        if (issuable == null) {
            return null;
        }
        return issuable.getCode();
    }

    @Override
    public Issuable convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Issuable.values())
                .filter(issuable -> issuable.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not parse issuable: " + code));
    }
}
