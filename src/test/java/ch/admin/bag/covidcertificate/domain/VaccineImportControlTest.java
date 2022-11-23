package ch.admin.bag.covidcertificate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VaccineImportControlTest {

    private VaccineImportControl source;

    private VaccineImportControl newObject;

    @BeforeEach
    void setUp() {
        // source equals newObject before any test
        LocalDateTime now = LocalDateTime.now();
        source = VaccineImportControl.builder()
                .importVersion("2.9.0")
                .importDate(LocalDate.now())
                .done(false)
                .build();
        newObject = VaccineImportControl.builder()
                .importVersion(source.getImportVersion())
                .importDate(source.getImportDate())
                .done(source.isDone())
                .build();
    }

    @Test
    void calling_equals_results_true() {
        // given
        // the objects from setUp

        // when
        boolean result = newObject.equals(source);
        // then
        assertThat(result).isTrue();
    }

    @Test
    void calling_equals_results_false_differentObject() {
        // given
        // the objects from setUp

        // when
        boolean result = newObject.equals("a string");
        // then
        assertThat(result).isFalse();
    }

    @Test
    void calling_equals_results_false_differentId() {
        // given
        // the objects from setUp
        // modified ID
        newObject.importVersion = "2.10.0";

        // when
        boolean result = newObject.equals(source);
        // then
        assertThat(result).isFalse();
    }

    @Test
    void calling_hashCode_delivers_same_value_as_we_have_sameId() {
        // given
        // the objects from setUp

        // when then
        assertThat(newObject).hasSameHashCodeAs(source);
    }

    @Test
    void calling_hashCode_delivers_different_values_as_we_have_differentIds() {
        // given
        // the objects from setUp
        // modified ID
        newObject.importVersion = "2.10.0";

        // when then
        assertThat(newObject).doesNotHaveSameHashCodeAs(source);
    }
}