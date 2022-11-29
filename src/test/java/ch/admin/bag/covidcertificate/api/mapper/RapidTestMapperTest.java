package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RapidTestMapperTest {

    public static final String RAPID_TEST_CODE_ONE = "One";
    public static final String RAPID_TEST_DISPLAY_ONE = "RapidTest One";
    public static final String RAPID_TEST_CODE_TWO = "Two";
    public static final String RAPID_TEST_DISPLAY_TWO = "RapidTest Two";
    public static final String EXPECTED_TO_STRING_RESULT = "TestDto(super=IssuableTestDto(code=One, display=RapidTest One, testType=null, validUntil=2023-12-31T23:59:59.000999999+01:00[Europe/Zurich]), active=true)";
    public static final String EXPECTED_EXCEPTION_MESSAGE = "Cannot invoke \"ch.admin.bag.covidcertificate.domain.RapidTest.getCode()\" because \"rapidTest\" is null";
    private RapidTest rapidTest;

    private List<RapidTest> rapidTests;

    @BeforeEach
    void setUpSourceObjects() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        ZonedDateTime tomorrow = ZonedDateTime.of(2023, 12, 31, 23, 59, 59, 999999, ZoneId.systemDefault());
        rapidTest = RapidTest.builder()
                .id(UUID.randomUUID())
                .code(RAPID_TEST_CODE_ONE)
                .display(RAPID_TEST_DISPLAY_ONE)
                .active(true)
                .modifiedAt(yesterday)
                .validUntil(tomorrow)
                .build();
        RapidTest rapidTestTwo = RapidTest.builder()
                .id(UUID.randomUUID())
                .code(RAPID_TEST_CODE_TWO)
                .display(RAPID_TEST_DISPLAY_TWO)
                .active(true)
                .modifiedAt(yesterday)
                .validUntil(tomorrow)
                .build();
        rapidTests = List.of(rapidTest, rapidTestTwo);
    }

    @Test
    void success_fromRapidTest() {
        TestDto testDto = RapidTestMapper.fromRapidTest(rapidTest);
        assertThat(testDto).isNotNull().asString().isEqualTo(EXPECTED_TO_STRING_RESULT);
    }

    @Test
    void exception_fromRapidTest_ifGivenVaccineIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            RapidTestMapper.fromRapidTest(null);
        });
        Assertions.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    void success_fromRapidTests() {
        List<TestDto> testDTOs = RapidTestMapper.fromRapidTests(rapidTests);
        assertThat(testDTOs).isNotNull().hasSize(2);
    }

    @Test
    void emptyList_fromRapidTests_ifGivenListIsNull() {
        List<TestDto> testDTOs = RapidTestMapper.fromRapidTests(null);
        assertThat(testDTOs).isNotNull().isEmpty();
    }
}