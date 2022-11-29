package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VaccineMapperTest {

    public static final String PROPHYLAXIS_ONE = "ProphylaxisOne";
    public static final String SOME_PROPHYLAXIS = "Some Prophylaxis";
    public static final String AUTH_HOLDER_ONE = "AuthHolderOne";
    public static final String SOME_AUTH_HOLDER = "Some AuthHolder";
    public static final String VACCINE_CODE_ONE = "One";
    public static final String VACCINE_DISPLAY_ONE = "Vaccine one";
    public static final String VACCINE_CODE_TWO = "Two";
    public static final String VACCINE_DISPLAY_TWO = "Vaccine two";
    public static final String EXPECTED_TO_STRING_RESULT = "VaccineDto(super=IssuableVaccineDto(productCode=One, productDisplay=Vaccine one, prophylaxisCode=ProphylaxisOne, prophylaxisDisplay=Some Prophylaxis, authHolderCode=AuthHolderOne, authHolderDisplay=Some AuthHolder, issuable=CH_AND_ABROAD, touristVaccine=true), active=true)";
    public static final String EXPECTED_EXCEPTION_MESSAGE = "Cannot invoke \"ch.admin.bag.covidcertificate.domain.Vaccine.getCode()\" because \"vaccine\" is null";
    private Vaccine vaccineOne;
    private List<Vaccine> vaccines;

    @BeforeEach
    void setUpSourceObjects() {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Prophylaxis prophylaxis = Prophylaxis.builder()
                .id(UUID.randomUUID())
                .code(PROPHYLAXIS_ONE)
                .display(SOME_PROPHYLAXIS)
                .active(true)
                .createdAt(yesterday)
                .modifiedAt(yesterday)
                .build();
        AuthHolder authHolder = AuthHolder.builder()
                .id(UUID.randomUUID())
                .code(AUTH_HOLDER_ONE)
                .display(SOME_AUTH_HOLDER)
                .active(true)
                .createdAt(yesterday)
                .modifiedAt(yesterday)
                .build();
        vaccineOne = Vaccine.builder()
                .id(UUID.randomUUID())
                .code(VACCINE_CODE_ONE)
                .display(VACCINE_DISPLAY_ONE)
                .active(true)
                .issuable(Issuable.CH_AND_ABROAD)
                .whoEul(true)
                .prophylaxis(prophylaxis)
                .authHolder(authHolder)
                .createdAt(yesterday)
                .modifiedAt(yesterday)
                .build();
        Vaccine vaccineTwo = Vaccine.builder()
                .id(UUID.randomUUID())
                .code(VACCINE_CODE_TWO)
                .display(VACCINE_DISPLAY_TWO)
                .active(true)
                .issuable(Issuable.CH_ONLY)
                .whoEul(true)
                .prophylaxis(prophylaxis)
                .authHolder(authHolder)
                .createdAt(yesterday)
                .modifiedAt(yesterday)
                .build();
        Vaccine vaccineDuplicateOfTwo = Vaccine.builder()
                .id(UUID.randomUUID())
                .code(vaccineTwo.getCode())
                .display(vaccineTwo.getDisplay())
                .active(true)
                .issuable(Issuable.CH_AND_ABROAD)
                .whoEul(true)
                .prophylaxis(prophylaxis)
                .authHolder(authHolder)
                .createdAt(yesterday)
                .modifiedAt(yesterday)
                .build();
        vaccines = List.of(vaccineOne, vaccineTwo, vaccineDuplicateOfTwo);
    }

    @Test
    void success_fromVaccine() {
        VaccineDto vaccineDto = VaccineMapper.fromVaccine(vaccineOne);
        assertThat(vaccineDto).isNotNull().asString().isEqualTo(EXPECTED_TO_STRING_RESULT);
    }

    @Test()
    void exception_fromVaccine_ifGivenVaccineIsNull() {
        Exception exception = Assertions.assertThrows(NullPointerException.class, () -> {
            VaccineMapper.fromVaccine(null);
        });
        Assertions.assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    void fromVaccines() {
        List<VaccineDto> vaccineDTOs = VaccineMapper.fromVaccines(vaccines);
        assertThat(vaccineDTOs).isNotNull().hasSize(3);
    }

    @Test
    void uniqueVaccines() {
        List<VaccineDto> vaccineDTOs = VaccineMapper.uniqueVaccines(vaccines);
        assertThat(vaccineDTOs).isNotNull().hasSize(2);
    }

    @Test
    void emptyList_fromVaccines_ifGivenListIsNull() {
        List<VaccineDto> vaccineDTOs = VaccineMapper.fromVaccines(null);
        assertThat(vaccineDTOs).isNotNull().isEmpty();
    }

    @Test
    void emptyList_uniqueVaccines_ifGivenListIsNull() {
        List<VaccineDto> vaccineDTOs = VaccineMapper.uniqueVaccines(null);
        assertThat(vaccineDTOs).isNotNull().isEmpty();
    }
}