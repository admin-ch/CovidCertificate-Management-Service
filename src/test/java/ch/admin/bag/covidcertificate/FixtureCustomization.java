package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import com.flextrade.jfixture.JFixture;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.util.ReflectionTestUtils;

import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.DE;

public class FixtureCustomization {
    public static void customizeVaccinationValueSet(JFixture fixture){
        fixture.customise().lazyInstance(VaccinationValueSet.class, () -> {
            var vaccinationValueSet = new VaccinationValueSet();
            ReflectionTestUtils.setField(vaccinationValueSet, "medicinalProduct", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "medicinalProductCode", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "prophylaxis", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "prophylaxisCode", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "authHolder", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "authHolderCode", fixture.create(String.class));
            ReflectionTestUtils.setField(vaccinationValueSet, "active", fixture.create(Boolean.class));
            return vaccinationValueSet;
        });
    }

    public static void customizeTestValueSet(JFixture fixture){
        fixture.customise().lazyInstance(TestValueSet.class, () -> {
            var testValueSet = new TestValueSet();
            ReflectionTestUtils.setField(testValueSet, "name", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "type", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "typeCode", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "manufacturer", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "swissTestKit", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "manufacturerCodeEu", fixture.create(String.class));
            ReflectionTestUtils.setField(testValueSet, "euAccepted", fixture.create(Boolean.class));
            ReflectionTestUtils.setField(testValueSet, "chAccepted", fixture.create(Boolean.class));
            ReflectionTestUtils.setField(testValueSet, "active", fixture.create(Boolean.class));
            return testValueSet;
        });
    }

    public static void customizeCountryCode(JFixture fixture){
        fixture.customise().lazyInstance(CountryCode.class, () -> {
            var countryCode = new CountryCode();
            ReflectionTestUtils.setField(countryCode, "shortName", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "display", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "lang", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "active", fixture.create(Boolean.class));
            ReflectionTestUtils.setField(countryCode, "version", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "system", fixture.create(String.class));
            return countryCode;
        });
    }

    public static void customizeVaccinationCertificateCreateDto(JFixture fixture){
        fixture.customise().lazyInstance(VaccinationCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            customizeVaccinationCertificateDataDto(helperFixture);
            var vaccinationCertificateCreateDto = helperFixture.create(VaccinationCertificateCreateDto.class);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "language", DE);
            return vaccinationCertificateCreateDto;
        });
    }

    public static void customizeCovidCertificateAddressDto(JFixture fixture, CertificateCreateDto createDto, String field, Object value){
        fixture.customise().lazyInstance(CovidCertificateAddressDto.class, () -> {
            var covidCertificateAddressDto = new JFixture().create(CovidCertificateAddressDto.class);
            ReflectionTestUtils.setField(covidCertificateAddressDto, field, value);
            return covidCertificateAddressDto;
        });
        ReflectionTestUtils.setField(createDto, "address", fixture.create(CovidCertificateAddressDto.class));
    }

    private static void customizeVaccinationCertificateDataDto(JFixture fixture){
        fixture.customise().lazyInstance(VaccinationCertificateDataDto.class, () -> {
            var numberOfDoses = fixture.create(Integer.class)%9+1;
            var totalNumberOfDoses = numberOfDoses + (int) Math.ceil(Math.random()*(9-numberOfDoses));
            var vaccinationCertificateCreateDto = new JFixture().create(VaccinationCertificateDataDto.class);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "numberOfDoses", numberOfDoses);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "totalNumberOfDoses", totalNumberOfDoses);
            return vaccinationCertificateCreateDto;
        });
    }

    public static void customizeTestCertificateCreateDto(JFixture fixture){
        fixture.customise().lazyInstance(TestCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var testCertificateCreateDto = helperFixture.create(TestCertificateCreateDto.class);
            ReflectionTestUtils.setField(testCertificateCreateDto, "language", DE);
            return testCertificateCreateDto;
        });
    }

    public static void customizeRecoveryCertificateCreateDto(JFixture fixture){
        fixture.customise().lazyInstance(RecoveryCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var recoveryCertificateCreateDto = helperFixture.create(RecoveryCertificateCreateDto.class);
            ReflectionTestUtils.setField(recoveryCertificateCreateDto, "language", DE);
            return recoveryCertificateCreateDto;
        });
    }

    public static void customizeCreateCertificateException(JFixture fixture){
        fixture.customise().lazyInstance(CreateCertificateException.class, () -> {
            var createCertificateError = fixture.create(CreateCertificateError.class);
            return new CreateCertificateException(createCertificateError);
        });
    }

    public static void customizeRevocationDto(JFixture fixture){
        fixture.customise().lazyInstance(RevocationDto.class, () -> new RevocationDto(createUVCI()));
    }

    private static String createUVCI(){
        return "urn:uvci:01:CH:"+RandomStringUtils.randomAlphanumeric(24).toUpperCase();
    }
}
