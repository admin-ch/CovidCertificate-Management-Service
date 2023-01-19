package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeliveryConverterTest {

    private DeliveryConverter converter;

    @BeforeEach
    void initialize() {
        this.converter = new DeliveryConverter();
    }

    @Test
    void successful_convertToDatabaseColumn_all_values() {
        assertThat(this.converter.convertToDatabaseColumn(Delivery.PRINT_BILLABLE)).isEqualTo("print_billable");
        assertThat(this.converter.convertToDatabaseColumn(Delivery.PRINT_NON_BILLABLE)).isEqualTo("print_non_billable");
        assertThat(this.converter.convertToDatabaseColumn(Delivery.APP)).isEqualTo("app");
        assertThat(this.converter.convertToDatabaseColumn(Delivery.OTHER)).isEqualTo("other");
        assertThat(this.converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void successful_convertToEntityAttribute_all_values() {
        assertThat(this.converter.convertToEntityAttribute("print_billable")).isEqualTo(Delivery.PRINT_BILLABLE);
        assertThat(this.converter.convertToEntityAttribute("print_non_billable")).isEqualTo(Delivery.PRINT_NON_BILLABLE);
        assertThat(this.converter.convertToEntityAttribute("app")).isEqualTo(Delivery.APP);
        assertThat(this.converter.convertToEntityAttribute("other")).isEqualTo(Delivery.OTHER);
        assertThat(this.converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void exception_convertToEntityAttribute_one_value() {
        assertThrows(IllegalArgumentException.class, () -> this.converter.convertToEntityAttribute("invalid"));
    }
}