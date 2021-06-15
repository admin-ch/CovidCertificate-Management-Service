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
import java.nio.file.Files;
import java.nio.file.Path;

import static ch.admin.bag.covidcertificate.TestModelProvider.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CovidPdfCertificateGenerationServiceTest {

    private CovidPdfCertificateGenerationService service;

    @Mock
    private ConfigurableEnvironment environment;

    @BeforeEach
    void setup() throws Exception {
        when(environment.getActiveProfiles()).thenReturn(new String[]{"unittest"});
        service = new CovidPdfCertificateGenerationService(environment);
    }


    @Test
    void generateDocument_vaccine() throws Exception {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto("1119349007");
        VaccinationValueSet vaccinationValueSet = new VaccinationValueSet();
        ReflectionTestUtils.setField(vaccinationValueSet, "prophylaxis", "SARS-CoV-2 mRNA vaccine");
        ReflectionTestUtils.setField(vaccinationValueSet, "medicinalProduct", "COVID-19 Vaccine Moderna");
        ReflectionTestUtils.setField(vaccinationValueSet, "authHolder", "Moderna Switzerland GmbH, Basel");
        String country = "Schweiz";
        String countryEn = "Switzerland";

        VaccinationCertificateQrCode qrCodeData = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(createDto, vaccinationValueSet);
        VaccinationCertificatePdf pdfData = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto, vaccinationValueSet, qrCodeData, country, countryEn);

        doTest(pdfData);

    }

    @Test
    void generateDocument_test() throws Exception {
        TestCertificateCreateDto createDto = getTestCertificateCreateDto("test", "test");
        TestValueSet testValueSet = new TestValueSet();
        ReflectionTestUtils.setField(testValueSet, "name", "Name 1");
        ReflectionTestUtils.setField(testValueSet, "type", "Rapid immunoassay");
        ReflectionTestUtils.setField(testValueSet, "manufacturer", "XXXXX, Basel");
        String country = "Schweiz";
        String countryEn = "Switzerland";

        TestCertificateQrCode qrCodeData = TestCertificateQrCodeMapper.toTestCertificateQrCode(createDto, testValueSet);
        TestCertificatePdf pdfData = TestCertificatePdfMapper.toTestCertificatePdf(createDto, testValueSet, qrCodeData, country, countryEn);

        doTest(pdfData);

    }

    @Test
    void generateDocument_recovery() throws Exception {
        RecoveryCertificateCreateDto createDto = getRecoveryCertificateCreateDto();
        String country = "Schweiz";
        String countryEn = "Switzerland";

        RecoveryCertificateQrCode qrCodeData = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
        RecoveryCertificatePdf pdfData = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, country, countryEn);

        doTest(pdfData);

    }

    void doTest(AbstractCertificatePdf pdfData) throws Exception {

        Barcode barcode = mock(Barcode.class);
        when(barcode.getPayload()).thenReturn("HC1:NCFOXNYTSFDHJI8-.O0:A%1W RI%.BI06%BF1WG21QKP85NPV*JVH5QWKIW18WA%NE/P3F/8X*G3M9FQH+4JZW4V/AY73CIBVQFSA36238FNB939PJ*KN%DJ3239L7BRNHKBWINEV40AT0C7LS4AZKZ73423ZQT-EJEG3LS4JXITAFK1HG%8SC91Z8YA7-TIP+PQE1W9L $N3-Q-*OGF2F%M RFUS2CPA-DG:A3AGJLC1788M7DD-I/2DBAJDAJCNB-439Y4.$SINOPK3.T4RZ4E%5MK9QM9DB9E%5:I9YHQ1FDIV4RB4VIOTNPS46UDBQEAJJKHHGQA8EL4QN9J9E6LF6JC1A5N11+N1X*8O13E20ZO8%3");


        byte[] document = service.generateCovidCertificate(pdfData, barcode);

        boolean storeDocument = false;

        if (storeDocument) {

            try {
                Files.delete(Path.of("/home/dev/Downloads/document.pdf"));
            } catch (Exception e) {

            }

            OutputStream out = new FileOutputStream("/home/dev/Downloads/document.pdf");
            out.write(document);
            out.close();
        }


    }


}
