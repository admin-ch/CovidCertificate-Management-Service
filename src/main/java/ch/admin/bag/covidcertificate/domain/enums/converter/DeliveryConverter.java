package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.Delivery;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class DeliveryConverter implements AttributeConverter<Delivery, String> {

    @Override
    public String convertToDatabaseColumn(Delivery delivery) {
        if (delivery == null) {
            return null;
        }
        return delivery.getCode();
    }

    @Override
    public Delivery convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Delivery.values())
                .filter(delivery -> delivery.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not parse delivery: " + code));
    }
}
