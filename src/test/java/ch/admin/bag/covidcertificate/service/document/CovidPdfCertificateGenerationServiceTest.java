package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.api.mapper.*;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import se.digg.dgc.encoding.Barcode;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ch.admin.bag.covidcertificate.TestModelProvider.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CovidPdfCertificateGenerationServiceTest {

    private CovidPdfCertificateGenerationService service;

    @Mock
    private ConfigurableEnvironment environment;

    private final String countryEn = "Switzerland";

    private final String familyNameBig = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    private final String givenNameBig = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

    private final String familyNameMiddle = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    private final String givenNameMiddle = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

    private final String familyNameSmall = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
    private final String givenNameSmall = "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";

    @BeforeEach
    void setup() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"unittest"});
        service = new CovidPdfCertificateGenerationService(environment);
    }


    private void generateDocument_vaccine(VaccinationCertificateCreateDto createDto, String language, String familyName, String givenName, String fileName) throws Exception {
        VaccinationValueSet vaccinationValueSet = new VaccinationValueSet();
        ReflectionTestUtils.setField(vaccinationValueSet, "prophylaxis", "SARS-CoV-2 mRNA vaccine");
        ReflectionTestUtils.setField(vaccinationValueSet, "medicinalProduct", "COVID-19 Vaccine Moderna");
        ReflectionTestUtils.setField(vaccinationValueSet, "authHolder", "Moderna Switzerland GmbH, Basel");
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);
        String country = "Schweiz";

        VaccinationCertificateQrCode qrCodeData = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(createDto, vaccinationValueSet);
        VaccinationCertificatePdf pdfData = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto, vaccinationValueSet, qrCodeData, country, countryEn);

        doTest(pdfData, fileName, language);

    }

    private void generateDocument_vaccine(String language, String familyName, String givenName) throws Exception {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto("1119349007", language);
        this.generateDocument_vaccine(createDto, language, familyName, givenName, "vaccine");
    }

    private void generateDocument_partialVaccination(String language, String familyName, String givenName) throws Exception {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto("1119349007", language);
        ReflectionTestUtils.setField(createDto.getVaccinationInfo().get(0), "numberOfDoses", 1);
        this.generateDocument_vaccine(createDto, language, familyName, givenName, "partial-vaccine");
    }

    private void generateDocument_test(String language, String familyName, String givenName) throws Exception {
        TestCertificateCreateDto createDto = getTestCertificateCreateDto("test", "test", language);
        TestValueSet testValueSet = new TestValueSet();
        ReflectionTestUtils.setField(testValueSet, "name", "Name 1");
        ReflectionTestUtils.setField(testValueSet, "type", "Rapid immunoassay");
        ReflectionTestUtils.setField(testValueSet, "manufacturer", "XXXXX, Basel");
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);
        String country = "Schweiz";

        TestCertificateQrCode qrCodeData = TestCertificateQrCodeMapper.toTestCertificateQrCode(createDto, testValueSet);
        TestCertificatePdf pdfData = TestCertificatePdfMapper.toTestCertificatePdf(createDto, testValueSet, qrCodeData, country, countryEn);

        doTest(pdfData, "test", language);

    }

    private void generateDocument_recovery(String language, String familyName, String givenName) throws Exception {
        RecoveryCertificateCreateDto createDto = getRecoveryCertificateCreateDto(language);
        String country = "Schweiz";
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "familyName", familyName);
        ReflectionTestUtils.setField(createDto.getPersonData().getName(), "givenName", givenName);

        RecoveryCertificateQrCode qrCodeData = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
        RecoveryCertificatePdf pdfData = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, country, countryEn);
        doTest(pdfData, "recovery", language);

    }


    @Test
    void generateAllDocuments() throws Exception {
        generateAllDocuments(familyNameSmall, givenNameSmall);
        generateAllDocuments(familyNameMiddle, givenNameMiddle);
        generateAllDocuments(familyNameBig, givenNameBig);

    }

    private void generateAllDocuments(String familyName, String givenName) throws Exception {
        generateDocument_recovery("de", familyName, givenName);
        generateDocument_recovery("fr", familyName, givenName);
        generateDocument_recovery("it", familyName, givenName);
        generateDocument_recovery("rm", familyName, givenName);

        generateDocument_vaccine("de", familyName, givenName);
        generateDocument_vaccine("fr", familyName, givenName);
        generateDocument_vaccine("it", familyName, givenName);
        generateDocument_vaccine("rm", familyName, givenName);

        generateDocument_test("de", familyName, givenName);
        generateDocument_test("fr", familyName, givenName);
        generateDocument_test("it", familyName, givenName);
        generateDocument_test("rm", familyName, givenName);

        generateDocument_partialVaccination("de", familyName, givenName);
        generateDocument_partialVaccination("fr", familyName, givenName);
        generateDocument_partialVaccination("it", familyName, givenName);
        generateDocument_partialVaccination("rm", familyName, givenName);
    }


    void doTest(AbstractCertificatePdf pdfData, String filename, String language) throws Exception {

        Barcode barcode = mock(Barcode.class);
        when(barcode.getPayload()).thenReturn("HC1:NCFOXNYTSFDHJI8-.O0:A%1W RI%.BI06%BF1WG21QKP85NPV*JVH5QWKIW18WA%NE/P3F/8X*G3M9FQH+4JZW4V/AY73CIBVQFSA36238FNB939PJ*KN%DJ3239L7BRNHKBWINEV40AT0C7LS4AZKZ73423ZQT-EJEG3LS4JXITAFK1HG%8SC91Z8YA7-TIP+PQE1W9L $N3-Q-*OGF2F%M RFUS2CPA-DG:A3AGJLC1788M7DD-I/2DBAJDAJCNB-439Y4.$SINOPK3.T4RZ4E%5MK9QM9DB9E%5:I9YHQ1FDIV4RB4VIOTNPS46UDBQEAJJKHHGQA8EL4QN9J9E6LF6JC1A5N11+N1X*8O13E20ZO8%3");


        byte[] document = service.generateCovidCertificate(pdfData, barcode);

        boolean storeDocument = false;

        if (storeDocument) {
            OutputStream out = new FileOutputStream("/home/dev/Downloads/certificate-" + filename + "-" + language + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss")) + ".pdf");
            out.write(document);
            out.close();
        }
    }
}
