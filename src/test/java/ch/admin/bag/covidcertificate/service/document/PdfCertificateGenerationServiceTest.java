package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.service.document.util.PdfHtmlRenderer;
import ch.admin.bag.covidcertificate.service.domain.pdf.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationTouristCertificatePdf;
import com.flextrade.jfixture.JFixture;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeAntibodyCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRecoveryCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificatePdf;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationTouristCertificatePdf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class PdfCertificateGenerationServiceTest {

    private final PdfCertificateGenerationService service = new PdfCertificateGenerationService();

    private final JFixture fixture = new JFixture();

    @BeforeEach
    void setup() {
        customizeVaccinationCertificatePdf(fixture);
        customizeTestCertificatePdf(fixture);
        customizeRecoveryCertificatePdf(fixture);
        customizeAntibodyCertificatePdf(fixture);
        customizeVaccinationTouristCertificatePdf(fixture);
    }

    @Nested
    class GenerateCovidCertificate {
        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void shouldCreateANewPdfRendererAtEachCall(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<PdfHtmlRenderer> pdfHtmlRendererMockConstructor = Mockito.mockConstruction(PdfHtmlRenderer.class,
                    (mock, context) -> when(mock.render(any(), any(), any())).thenReturn(fixture.create(String.class)));
                 MockedConstruction<PdfRendererBuilder> pdfRendererBuilderMockedConstruction = Mockito.mockConstruction(PdfRendererBuilder.class,
                         (mock, context) -> doNothing().when(mock).run())) {

                service.generateCovidCertificate(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());
                service.generateCovidCertificate(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());

                assertEquals(2, pdfHtmlRendererMockConstructor.constructed().size());
            }
        }

        @ParameterizedTest
        @ValueSource(classes = {VaccinationCertificatePdf.class, TestCertificatePdf.class, RecoveryCertificatePdf.class, AntibodyCertificatePdf.class, VaccinationTouristCertificatePdf.class})
        void shouldCreateANewPdfRendererBuilderAtEachCall(Class<? extends AbstractCertificatePdf> clazz) {
            try (MockedConstruction<PdfHtmlRenderer> pdfHtmlRendererMockConstructor = Mockito.mockConstruction(PdfHtmlRenderer.class,
                    (mock, context) -> when(mock.render(any(), any(), any())).thenReturn(fixture.create(String.class)));
                 MockedConstruction<PdfRendererBuilder> pdfRendererBuilderMockedConstruction = Mockito.mockConstruction(PdfRendererBuilder.class,
                         (mock, context) -> doNothing().when(mock).run())) {

                service.generateCovidCertificate(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());
                service.generateCovidCertificate(fixture.create(clazz), fixture.create(String.class), LocalDateTime.now());

                assertEquals(2, pdfRendererBuilderMockedConstruction.constructed().size());
            }
        }
    }
}
