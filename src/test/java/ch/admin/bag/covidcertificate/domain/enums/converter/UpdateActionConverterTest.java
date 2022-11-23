package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateActionConverterTest {

    private UpdateActionConverter converter;

    @BeforeEach
    void initialize() {
        this.converter = new UpdateActionConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        assertThat(this.converter.convertToDatabaseColumn(UpdateAction.NEW)).isEqualTo("new");
        assertThat(this.converter.convertToDatabaseColumn(UpdateAction.UPDATE)).isEqualTo("update");
        assertThat(this.converter.convertToDatabaseColumn(UpdateAction.DELETE)).isEqualTo("delete");
    }

    @Test
    void convertToEntityAttribute() {
        assertThat(this.converter.convertToEntityAttribute("new")).isEqualTo(UpdateAction.NEW);
        assertThat(this.converter.convertToEntityAttribute("update")).isEqualTo(UpdateAction.UPDATE);
        assertThat(this.converter.convertToEntityAttribute("delete")).isEqualTo(UpdateAction.DELETE);
    }

    @Test
    void exception_convertToEntityAttribute_one_value() {
        assertThrows(IllegalArgumentException.class, () -> this.converter.convertToEntityAttribute("invalid"));
    }
}