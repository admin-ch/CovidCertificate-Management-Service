package ch.admin.bag.covidcertificate.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProphylaxisTest {


    private Prophylaxis source;

    private Prophylaxis newObject;

    @BeforeEach
    void setUp() {
        // source equals newObject before any test
        LocalDateTime now = LocalDateTime.now();
        source = Prophylaxis.builder()
                .id(UUID.randomUUID())
                .code("someCode")
                .display("some display name")
                .active(true)
                .createdAt(now)
                .modifiedAt(now)
                .build();
        newObject = Prophylaxis.builder()
                .id(source.getId())
                .code(source.getCode())
                .display(source.getDisplay())
                .active(source.isActive())
                .createdAt(source.getCreatedAt())
                .modifiedAt(source.getModifiedAt())
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
        // modified Id
        newObject.id = UUID.randomUUID();

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
        assertThat(newObject.hashCode()).isEqualTo(source.hashCode());
    }

    @Test
    void calling_hashCode_delivers_different_values_as_we_have_differentIds() {
        // given
        // the objects from setUp
        // modified Id
        newObject.id = UUID.randomUUID();

        // when then
        assertThat(newObject.hashCode()).isNotEqualTo(source.hashCode());
    }
}