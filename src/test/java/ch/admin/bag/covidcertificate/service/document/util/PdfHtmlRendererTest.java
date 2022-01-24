package ch.admin.bag.covidcertificate.service.document.util;

import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeAntibodyCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRecoveryCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationTouristCertificatePdf;
import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class PdfHtmlRendererTest {
    private final boolean showWatermark = true;
    private final PdfHtmlRenderer pdfHtmlRenderer = new PdfHtmlRenderer(showWatermark);
    private final JFixture fixture = new JFixture();
    @Mock
    private TemplateEngine templateEngine;

    @BeforeEach
    void setup() {
        customizeVaccinationCertificatePdf(fixture);
        customizeTestCertificatePdf(fixture);
        customizeRecoveryCertificatePdf(fixture);
        customizeAntibodyCertificatePdf(fixture);
        customizeVaccinationTouristCertificatePdf(fixture);
        ReflectionTestUtils.setField(pdfHtmlRenderer, "templateEngine", templateEngine);
        lenient().when(templateEngine.process(anyString(), any(Context.class))).thenReturn(fixture.create(String.class));
    }

    @Nested
    class Render {

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.service.document.util.PdfHtmlRendererTest#languagesAndLocales")
        void setsLocaleVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz, String language, Locale locale) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                ReflectionTestUtils.setField(data, "language", language);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setLocale(locale);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsDataVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("data", data);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsShowValidOnlyInSwitzerlandVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("showValidOnlyInSwitzerland", data.showValidOnlyInSwitzerland());
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsIsEvidenceVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("isEvidence", data.isEvidence());
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsShowWatermarkVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("showWatermark", showWatermark);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsQrCodeVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var barcode = fixture.create(String.class);
                pdfHtmlRenderer.render(fixture.create(clazz), barcode, LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("qrCode", barcode);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsDateFormatterVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                pdfHtmlRenderer.render(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("dateFormatter", LOCAL_DATE_FORMAT);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsCreationDateVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var issuedAt = LocalDateTime.now();
                pdfHtmlRenderer.render(fixture.create(clazz), fixture.create(String.class), issuedAt);

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("creationDate", issuedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsCreationTimeVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var issuedAt = LocalDateTime.now();
                pdfHtmlRenderer.render(fixture.create(clazz), fixture.create(String.class), issuedAt);

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("creationTime", issuedAt.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsBirthdateVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("birthdate", DateHelper.formatDateOfBirth(data.getDateOfBirth()));
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsDateTimeFormatterVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            var dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class);
                 MockedStatic<DateTimeFormatter> mockedDateTimeFormatter = Mockito.mockStatic(DateTimeFormatter.class)) {
                mockedDateTimeFormatter.when(() -> DateTimeFormatter.ofPattern(any())).thenReturn(dateTimeFormatter);
                pdfHtmlRenderer.render(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                mockedDateTimeFormatter.verify(() -> DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
                verify(mockedConstruction.constructed().get(0)).setVariable("dateTimeFormatter", dateTimeFormatter);
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void setsTypeVariableCorrectly(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<Context> mockedConstruction = Mockito.mockConstruction(Context.class)) {
                var data = fixture.create(clazz);
                pdfHtmlRenderer.render(data, fixture.create(String.class), LocalDateTime.now());

                assertEquals(1, mockedConstruction.constructed().size());
                verify(mockedConstruction.constructed().get(0)).setVariable("type", data.getType().toString());
            }
        }
    }

    private static Stream<Arguments> languagesAndLocales() {
        return Stream.of(VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class)
                .flatMap(clazz -> Stream.of(
                        Arguments.of(clazz, "fr", Locale.FRENCH),
                        Arguments.of(clazz, "it", Locale.ITALIAN),
                        Arguments.of(clazz, "rm", Locale.forLanguageTag("rm")),
                        Arguments.of(clazz, "de", Locale.GERMAN))
                );
    }
}