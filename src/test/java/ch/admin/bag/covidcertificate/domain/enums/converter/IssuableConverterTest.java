package ch.admin.bag.covidcertificate.domain.enums.converter;

import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IssuableConverterTest {

    private IssuableConverter converter;

    @BeforeEach
    void initialize() {
        this.converter = new IssuableConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        assertThat(this.converter.convertToDatabaseColumn(Issuable.UNDEFINED)).isEqualTo("undefined");
        assertThat(this.converter.convertToDatabaseColumn(Issuable.CH_ONLY)).isEqualTo("ch_only");
        assertThat(this.converter.convertToDatabaseColumn(Issuable.ABROAD_ONLY)).isEqualTo("abroad_only");
        assertThat(this.converter.convertToDatabaseColumn(Issuable.CH_AND_ABROAD)).isEqualTo("ch_and_abroad");
        assertThat(this.converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute() {
        assertThat(this.converter.convertToEntityAttribute("undefined")).isEqualTo(Issuable.UNDEFINED);
        assertThat(this.converter.convertToEntityAttribute("ch_only")).isEqualTo(Issuable.CH_ONLY);
        assertThat(this.converter.convertToEntityAttribute("abroad_only")).isEqualTo(Issuable.ABROAD_ONLY);
        assertThat(this.converter.convertToEntityAttribute("ch_and_abroad")).isEqualTo(Issuable.CH_AND_ABROAD);
        assertThat(this.converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void exception_convertToEntityAttribute_one_value() {
        assertThrows(IllegalArgumentException.class, () -> this.converter.convertToEntityAttribute("invalid"));
    }
}