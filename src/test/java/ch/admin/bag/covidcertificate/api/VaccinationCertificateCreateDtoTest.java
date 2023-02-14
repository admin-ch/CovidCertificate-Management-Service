package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class VaccinationCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final VaccinationCertificateDataDto dataDto = mock(VaccinationCertificateDataDto.class);

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeClass
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void testNoVaccinationData() {
        String language = "de";
        VaccinationCertificateCreateDto testee = new VaccinationCertificateCreateDto(
                personDto,
                null,
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        Set<ConstraintViolation<VaccinationCertificateCreateDto>> violations = validator.validateProperty(testee, "vaccinationInfo");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No vaccination data was specified")));

        testee = new VaccinationCertificateCreateDto(
                personDto,
                List.of(),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );

        violations = validator.validateProperty(testee, "certificateData");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No vaccination data was specified")));

        testee = new VaccinationCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        violations = validator.validateProperty(testee, "certificateData");
        assertTrue(violations.isEmpty());
    }
}
