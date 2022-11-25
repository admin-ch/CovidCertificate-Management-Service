package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityTypeConverterTest {

    private EntityTypeConverter converter;

    @BeforeEach
    void initialize() {
        this.converter = new EntityTypeConverter();
    }

    @Test
    void successful_convertToDatabaseColumn_all_values() {
        assertThat(this.converter.convertToDatabaseColumn(EntityType.VACCINE)).isEqualTo("Vaccine");
        assertThat(this.converter.convertToDatabaseColumn(EntityType.AUTH_HOLDER)).isEqualTo("AuthHolder");
        assertThat(this.converter.convertToDatabaseColumn(EntityType.PROPHYLAXIS)).isEqualTo("Prophylaxis");
        assertThat(this.converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void successful_convertToEntityAttribute_all_values() {
        assertThat(this.converter.convertToEntityAttribute("Vaccine")).isEqualTo(EntityType.VACCINE);
        assertThat(this.converter.convertToEntityAttribute("AuthHolder")).isEqualTo(EntityType.AUTH_HOLDER);
        assertThat(this.converter.convertToEntityAttribute("Prophylaxis")).isEqualTo(EntityType.PROPHYLAXIS);
        assertThat(this.converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void exception_convertToEntityAttribute_one_value() {
        assertThrows(IllegalArgumentException.class, () -> this.converter.convertToEntityAttribute("invalid"));
    }
}