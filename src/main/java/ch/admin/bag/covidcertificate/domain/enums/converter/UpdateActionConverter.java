package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class UpdateActionConverter implements AttributeConverter<UpdateAction, String> {

    @Override
    public String convertToDatabaseColumn(UpdateAction updateAction) {
        if (updateAction == null) {
            return null;
        }
        return updateAction.getCode();
    }

    @Override
    public UpdateAction convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(UpdateAction.values())
                .filter(updateAction -> updateAction.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not parse updateAction: " + code));
    }
}
