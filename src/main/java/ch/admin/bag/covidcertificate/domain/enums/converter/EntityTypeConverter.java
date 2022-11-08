package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class EntityTypeConverter implements AttributeConverter<EntityType, String> {

    @Override
    public String convertToDatabaseColumn(EntityType entityType) {
        if (entityType == null) {
            return null;
        }
        return entityType.getCode();
    }

    @Override
    public EntityType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(EntityType.values())
                .filter(entityType -> entityType.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not parse entityType: " + code));
    }
}
