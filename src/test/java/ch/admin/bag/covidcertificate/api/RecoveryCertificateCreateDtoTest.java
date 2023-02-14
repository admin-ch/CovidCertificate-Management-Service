package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NO_RECOVERY_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class RecoveryCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final RecoveryCertificateDataDto dataDto = mock(RecoveryCertificateDataDto.class);

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
    public void testNoRecoveryData() {
        String language = "de";
        RecoveryCertificateCreateDto testee = new RecoveryCertificateCreateDto(
                personDto,
                null,
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isRecoveryDataValid").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isRecoveryDataValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No recovery data specified")));


        testee = new RecoveryCertificateCreateDto(
                personDto,
                List.of(),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isRecoveryDataValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isRecoveryDataValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No recovery data specified")));

        testee = new RecoveryCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isRecoveryDataValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isRecoveryDataValid());
        assertTrue(violations.isEmpty());

    }
}
