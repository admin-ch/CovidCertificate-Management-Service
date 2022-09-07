package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CovidCertificateGeneratePdfFromExistingServiceTest {
    private final JFixture fixture = new JFixture();
    @InjectMocks
    private CovidCertificateGeneratePdfFromExistingService service;
    @Mock
    private BarcodeService barcodeService;
    @Mock
    private PdfCertificateGenerationService pdfCertificateGenerationService;
    @Mock
    private CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    @Mock
    private CovidCertificatePdfGenerateRequestDtoMapperService covidCertificatePdfGenerateRequestDtoMapperService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private COSETime coseTime;

    @BeforeEach
    public void setUp() throws IOException {
        lenient().when(barcodeService.createBarcode(any(), any(), any())).thenReturn(fixture.create(Barcode.class));
        lenient().when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any()))
                 .thenReturn(fixture.create(byte[].class));

        lenient().when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any()))
                 .thenReturn(fixture.create(VaccinationCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(VaccinationCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificateQrCode(any()))
                 .thenReturn(fixture.create(TestCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(TestCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any()))
                 .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(RecoveryCertificatePdf.class));

        lenient().when(coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS))
                 .thenReturn(fixture.create(Instant.class));
        lenient().when(coseTime.calculateExpirationInstantPlusDays(Constants.EXPIRATION_PERIOD_30_DAYS))
                 .thenReturn(fixture.create(Instant.class));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        lenient().when(objectMapper.writer()).thenReturn(objectWriter);
        lenient().when(objectWriter.writeValueAsString(any())).thenReturn(fixture.create(String.class));
    }

    LocalDateTime getLocalDateTimeFromEpochMillis(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).toLocalDateTime();
    }

    @Nested
    class GenerateVaccinationFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toVaccinationCertificatePdf(
                    pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(
                    any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var vaccinationPdf = fixture.create(VaccinationCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(
                    any())).thenReturn(vaccinationPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(vaccinationPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateVaccinationTouristFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationTouristCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toVaccinationTouristCertificatePdf(
                    pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationTouristCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationTouristCertificatePdf(
                    any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var vaccinationTouristPdf = fixture.create(VaccinationTouristCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationTouristCertificatePdf(
                    any())).thenReturn(vaccinationTouristPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(vaccinationTouristPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateTestFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToTestCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toTestCertificatePdf(pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToTestCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toTestCertificatePdf(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var testPdf = fixture.create(TestCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toTestCertificatePdf(any())).thenReturn(testPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(testPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateRecoveryFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toRecoveryCertificatePdf(pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(any())).thenThrow(
                    expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var recoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(any())).thenReturn(
                    recoveryPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(recoveryPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateRecoveryRatFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toRecoveryRatCertificatePdf(
                    pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryRatCertificatePdf(any())).thenThrow(
                    expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var recoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryRatCertificatePdf(any())).thenReturn(
                    recoveryPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(recoveryPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateAntibodyFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toAntibodyCertificatePdf(pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toAntibodyCertificatePdf(any())).thenThrow(
                    expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var recoveryPdf = fixture.create(AntibodyCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toAntibodyCertificatePdf(any())).thenReturn(
                    recoveryPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(recoveryPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }

    @Nested
    class GenerateExceptionalFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toExceptionalCertificatePdf(
                    pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toExceptionalCertificatePdf(any())).thenThrow(
                    expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateFromExistingCovidCertificate(
                                                                        pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var recoveryPdf = fixture.create(ExceptionalCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toExceptionalCertificatePdf(any())).thenReturn(
                    recoveryPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(recoveryPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), eq(barcode.getPayload()), any());
        }

        @Test
        void shouldCreatePdf_withCorrectIssuedAt() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var issuedAt = getLocalDateTimeFromEpochMillis(pdfGenerateRequestDto.getIssuedAt());

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(any(), any(), eq(issuedAt));
        }

        @Test
        void shouldReturnBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(),
                                                             StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }
    }
}
