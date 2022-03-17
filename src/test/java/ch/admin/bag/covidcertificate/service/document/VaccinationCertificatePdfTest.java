package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.Issuable;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.validator.UvciValidator;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.CountryCodesLoader;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfTextLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.appName;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.birthDateLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.birthDateLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.birthDateValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.certificateTitlePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.certificateTitleSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.countryOfVaccinationLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.countryOfVaccinationLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.countryOfVaccinationValuePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.countryOfVaccinationValueSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.creationDateTextPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.creationDateTextSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.disclaimerValuePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.disclaimerValueSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.diseaseLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.diseaseLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.diseaseValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.documentTitlePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.documentTitleSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.dosisLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.dosisLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.dosisValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.getRegionLocators;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.infoLineValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.issuerLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.issuerLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.issuerValuePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.issuerValueSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.manufacturerLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.manufacturerLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.manufacturerValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.nameLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.nameLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.nameValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.personalInfoTitlePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.personalInfoTitleTitleSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.productLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.productLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.productValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.useAppValuePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.useAppValueSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.usedDateFormat;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.uvci;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccinationDateLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccinationDateLablePrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccinationDateValue;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccineTypeLabelPrimaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccineTypeLabelSecondaryLang;
import static ch.admin.bag.covidcertificate.testutil.pdf.locator.PdfCertificatesRegionsLocators.VaccinationCertificatesRegionsLocators.vaccineTypeValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@FunctionalInterface
interface assertPrimaryLanguage<One, Two, Three> {
    Three apply(One one, Two two);
}

@SpringBootTest(classes = {CountryCodesLoader.class, ObjectMapper.class})
@TestInstance(Lifecycle.PER_CLASS)
public class VaccinationCertificatePdfTest {

    private final String secondaryLanguage = "en";
    private final PdfCertificateGenerationService service = new PdfCertificateGenerationService();
    @TempDir
    Path tempDir;
    private Properties messagesSecondaryLanguage;
    private List<CountryCode> countriesSecondaryLanguage;
    private IssuableVaccineDto vaccineDto;
    @Autowired
    private CountryCodesLoader countryCodesLoader;


    @BeforeAll
    public void setUp() {
        //countryCodesLoader = new CountryCodesLoader(new ObjectMapper());
        try {
            messagesSecondaryLanguage = loadMessagesFile(secondaryLanguage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        countriesSecondaryLanguage = loadCountries(secondaryLanguage);
        vaccineDto = new IssuableVaccineDto("EU/1/20/1528",
                "Comirnaty",
                "1119349007",
                "SARS-CoV-2 mRNA vaccine",
                "ORG-100030215",
                "Biontech Manufacturing GmbH",
                Issuable.CH_AND_ABROAD,
                false);
    }

    private Properties loadMessagesFile(String language) throws FileNotFoundException {
        // String path = String.format("src/main/resources/templates/messages/document-messages_%s.properties", language);
        String path = String.format("templates/messages/document-messages_%s.properties", language);


        // new InputStreamReader( getClass().getClassLoader().getResourceAsStream(path), Charset.forName("UTF-8"));
        // new FileInputStream(path)


        try (InputStreamReader input = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        throw new FileNotFoundException("Messages document was not found!");
    }

    private List<CountryCode> loadCountries(String language) {
        switch (language) {
            case "en":
                return countryCodesLoader.getCountryCodes().getEn();
            case "de":
                return countryCodesLoader.getCountryCodes().getDe();
            case "fr":
                return countryCodesLoader.getCountryCodes().getFr();
            case "it":
                return countryCodesLoader.getCountryCodes().getIt();
            case "rm":
                return countryCodesLoader.getCountryCodes().getRm();
            default:
                throw new IllegalArgumentException(String.format("Language \"%s\" is not supported.", language));
        }
    }

    private void generateVaccinationCertificate(
            File file,
            String familyName,
            String givenName,
            LocalDate birthDate,
            IssuableVaccineDto issuableVaccineDto,
            int numberOfDoses,
            int totalNumberOfDoses,
            LocalDate vaccinationDate,
            CountryCode countryCodePrimaryLanguage,
            CountryCode countryCodeSecondaryLanguage,
            String language) {
        VaccinationCertificateCreateDto createDto = getVaccinationCertificateCreateDto(
                familyName, givenName,
                birthDate,
                issuableVaccineDto.getProphylaxisCode(),
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryCodePrimaryLanguage.getShortName(),
                language);
        VaccinationCertificateQrCode qrCodeData = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(
                createDto,
                issuableVaccineDto);
        VaccinationCertificatePdf pdfData = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto,
                issuableVaccineDto,
                qrCodeData,
                countryCodePrimaryLanguage.getDisplay(),
                countryCodeSecondaryLanguage.getDisplay());

        var barcodePayload = "HC1:NCFOXNYTSFDHJI8-.O0:A%1W RI%.BI06%BF1WG21QKP85NPV*JVH5QWKIW18WA%NE/P3F/8X*G3M9FQH+4JZW4V/AY73CIBVQFSA36238FNB939PJ*KN%DJ3239L7BRNHKBWINEV40AT0C7LS4AZKZ73423ZQT-EJEG3LS4JXITAFK1HG%8SC91Z8YA7-TIP+PQE1W9L $N3-Q-*OGF2F%M RFUS2CPA-DG:A3AGJLC1788M7DD-I/2DBAJDAJCNB-439Y4.$SINOPK3.T4RZ4E%5MK9QM9DB9E%5:I9YHQ1FDIV4RB4VIOTNPS46UDBQEAJJKHHGQA8EL4QN9J9E6LF6JC1A5N11+N1X*8O13E20ZO8%3";

        byte[] document = service.generateCovidCertificate(pdfData, barcodePayload, LocalDateTime.now());

        try (OutputStream out = new FileOutputStream(file.getAbsolutePath())) {
            out.write(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"de", "fr", "it", "rm"})
    @DisplayName("Given a vaccination certificate, when the PDF document is generated, all texts should be at their place.")
    void test1(String primaryLanguage) throws Exception {

        File certificate = tempDir.resolve(String.format("certificate_%s.pdf", primaryLanguage)).toFile();

        Properties messagesPrimaryLanguage = loadMessagesFile(primaryLanguage);
        List<CountryCode> countriesPrimaryLanguage = loadCountries(primaryLanguage);


        String familyName = "Do";
        String givenName = "John";
        LocalDate birthDate = LocalDate.of(1985, 9, 20);
        int numberOfDoses = 2;
        int totalNumberOfDoses = 2;
        LocalDate vaccinationDate = LocalDate.of(2022, 1, 1);
        String country = "CH";
        CountryCode countryPrimaryLanguage = countriesPrimaryLanguage.stream().filter(countryCode -> Objects.equals(countryCode.getShortName(), country)).findFirst().orElseThrow();
        CountryCode countrySecondaryLanguage = countriesSecondaryLanguage.stream().filter(countryCode -> Objects.equals(countryCode.getShortName(), country)).findFirst().orElseThrow();

        generateVaccinationCertificate(
                certificate,
                familyName,
                givenName,
                birthDate,
                vaccineDto,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryPrimaryLanguage,
                countrySecondaryLanguage,
                primaryLanguage
        );

        assertTrue(certificate.exists());
        assertTrue(certificate.getAbsolutePath().endsWith(String.format("certificate_%s.pdf", primaryLanguage)));

        PdfTextLocator pdfTextLocator = new PdfTextLocator(certificate.getParent(), certificate.getName(), 1);

        Map<String, String> pdfTexts = new HashMap<>();

        getRegionLocators(primaryLanguage).
                forEach(localisationData ->
                {
                    try {
                        String key = localisationData.getText();
                        String value = pdfTextLocator.readText(localisationData, Color.BLUE);
                        pdfTexts.put(key, value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        pdfTextLocator.saveAndClosePdfDocument();


        assertEquals(messagesPrimaryLanguage.getProperty("document.title"), pdfTexts.get(documentTitlePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("document.title"), pdfTexts.get(documentTitleSecondaryLang));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.title"), pdfTexts.get(certificateTitlePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.title"), pdfTexts.get(certificateTitleSecondaryLang));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.disease.label"), pdfTexts.get(diseaseLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.disease.label"), pdfTexts.get(diseaseLabelSecondaryLang));
        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.disease"), pdfTexts.get(diseaseValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.dosis.label"), pdfTexts.get(dosisLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.dosis.label"), pdfTexts.get(dosisLabelSecondaryLang));
        assertEquals(" / 2 2", pdfTexts.get(dosisValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.type.label"), pdfTexts.get(vaccineTypeLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.type.label"), pdfTexts.get(vaccineTypeLabelSecondaryLang));
        assertEquals(vaccineDto.getProphylaxisDisplay(), pdfTexts.get(vaccineTypeValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.product.label"), pdfTexts.get(productLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.product.label"), pdfTexts.get(productLabelSecondaryLang));
        assertEquals(vaccineDto.getProductDisplay(), pdfTexts.get(productValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.manufacturer.label"), pdfTexts.get(manufacturerLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.manufacturer.label"), pdfTexts.get(manufacturerLabelSecondaryLang));
        assertEquals(vaccineDto.getAuthHolderDisplay(), pdfTexts.get(manufacturerValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.date.label"), pdfTexts.get(vaccinationDateLablePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.date.label"), pdfTexts.get(vaccinationDateLabelSecondaryLang));
        assertEquals("01.01.2022", pdfTexts.get(vaccinationDateValue));

        assertEquals(messagesPrimaryLanguage.getProperty("vaccination.country.label"), pdfTexts.get(countryOfVaccinationLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("vaccination.country.label"), pdfTexts.get(countryOfVaccinationLabelSecondaryLang));
        assertEquals(countryPrimaryLanguage.getDisplay(), pdfTexts.get(countryOfVaccinationValuePrimaryLang));
        assertEquals(countrySecondaryLanguage.getDisplay(), pdfTexts.get(countryOfVaccinationValueSecondaryLang));

        assertDoesNotThrow(() -> UvciValidator.validateUvciMatchesSpecification(pdfTexts.get(uvci)));

        String creationDateTimeTextRegexFormat = "%s(3[01]|[12]\\d|0[1-9]).(1[012]|0[1-9]).(2\\d{3})%s([0-1][0-9]||2[0-3]):[0-5][0-9]";
        String creationDateTextPrimaryLangStr = pdfTexts.get(creationDateTextPrimaryLang);
        String qrCodeLabel = messagesPrimaryLanguage.getProperty("qrCode.label");
        String firstPart = StringUtils.substringBefore(qrCodeLabel, "{0}");
        String secondPart = StringUtils.substringBetween(qrCodeLabel, "{0}", "{1}");
        Pattern creationDateTextPattern = Pattern.compile(String.format(creationDateTimeTextRegexFormat, firstPart, secondPart));
        assertTrue(creationDateTextPattern.matcher(creationDateTextPrimaryLangStr).matches());

        String creationDateTextSecondaryLangStr = pdfTexts.get(creationDateTextSecondaryLang);
        qrCodeLabel = messagesSecondaryLanguage.getProperty("qrCode.label");
        firstPart = StringUtils.substringBefore(qrCodeLabel, "{0}");
        secondPart = StringUtils.substringBetween(qrCodeLabel, "{0}", "{1}");
        creationDateTextPattern = Pattern.compile(String.format(creationDateTimeTextRegexFormat, firstPart, secondPart));
        assertTrue(creationDateTextPattern.matcher(creationDateTextSecondaryLangStr).matches());

        assertEquals(messagesPrimaryLanguage.getProperty("qrCode.date.label"), pdfTexts.get(usedDateFormat));

        assertEquals(messagesPrimaryLanguage.getProperty("personalData.title"), pdfTexts.get(personalInfoTitlePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("personalData.title"), pdfTexts.get(personalInfoTitleTitleSecondaryLang));

        String nameFormat = "%1$s %2$s";
        assertEquals(messagesPrimaryLanguage.getProperty("personalData.name.label"), pdfTexts.get(nameLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("personalData.name.label"), pdfTexts.get(nameLabelSecondaryLang));
        assertEquals(String.format(nameFormat, familyName, givenName), pdfTexts.get(nameValue));

        assertEquals(messagesPrimaryLanguage.getProperty("personalData.date.label"), pdfTexts.get(birthDateLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("personalData.date.label"), pdfTexts.get(birthDateLabelSecondaryLang));
        assertEquals("20.09.1985", pdfTexts.get(birthDateValue));

        assertEquals(messagesPrimaryLanguage.getProperty("issuer.title"), pdfTexts.get(issuerLabelPrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("issuer.title"), pdfTexts.get(issuerLabelSecondaryLang));
        assertEquals(messagesPrimaryLanguage.getProperty("issuer.issuer"), pdfTexts.get(issuerValuePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("issuer.issuer"), pdfTexts.get(issuerValueSecondaryLang));

        String disclaimerPrimaryLanguageExpected = new StringJoiner(" ")
                .add(messagesPrimaryLanguage.getProperty("info.validOnlyOnIdPresentation"))
                .add(messagesPrimaryLanguage.getProperty("info.notTravelDocument"))
                .add(messagesPrimaryLanguage.getProperty("info.getInformationBeforeTravel"))
                .add(messagesPrimaryLanguage.getProperty("info.keepPaperDocument")).toString();

        String disclaimerSecondaryLanguageExpected = new StringJoiner(" ")
                .add(messagesSecondaryLanguage.getProperty("info.validOnlyOnIdPresentation"))
                .add(messagesSecondaryLanguage.getProperty("info.notTravelDocument"))
                .add(messagesSecondaryLanguage.getProperty("info.getInformationBeforeTravel"))
                .add(messagesSecondaryLanguage.getProperty("info.keepPaperDocument")).toString();

        String disclaimerValuePrimaryLangActual = pdfTexts.get(disclaimerValuePrimaryLang);
        String disclaimerValueSecondaryLangActual = pdfTexts.get(disclaimerValueSecondaryLang);

        assertFalse(disclaimerValuePrimaryLangActual.isBlank());
        assertFalse(disclaimerValueSecondaryLangActual.isBlank());

        assertTrue(disclaimerPrimaryLanguageExpected.replace(" ", "").equalsIgnoreCase(disclaimerValuePrimaryLangActual.replaceAll(" ", "")));
        assertTrue(disclaimerSecondaryLanguageExpected.replace(" ", "").equalsIgnoreCase(disclaimerValueSecondaryLangActual.replaceAll(" ", "")));

        assertEquals(messagesPrimaryLanguage.getProperty("footer.app"), pdfTexts.get(useAppValuePrimaryLang));
        assertEquals(messagesSecondaryLanguage.getProperty("footer.app"), pdfTexts.get(useAppValueSecondaryLang));

        assertEquals(messagesPrimaryLanguage.getProperty("footer.infoline"), pdfTexts.get(infoLineValue));

        assertEquals("Covid Certificate", pdfTexts.get(appName));
    }
}