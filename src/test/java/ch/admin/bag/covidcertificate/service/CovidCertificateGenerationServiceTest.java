package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import java.util.Base64;

import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CovidCertificateGenerationServiceTest {
    @InjectMocks
    private CovidCertificateGenerationService service;

    @Mock
    private BarcodeService barcodeService;
    @Mock
    private PdfCertificateGenerationService pdfCertificateGenerationService;
    @Mock
    private CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    @Mock
    private CovidCertificatePdfGenerateRequestDtoMapperService covidCertificatePdfGenerateRequestDtoMapperService;
    @Mock
    private CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PrintQueueClient printQueueClient;
    @Mock
    private InAppDeliveryClient inAppDeliveryClient;
    @Mock
    private SigningInformationService signingInformationService;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void setUp() throws IOException {
        lenient().when(barcodeService.createBarcode(any(), any())).thenReturn(fixture.create(Barcode.class));
        lenient().when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(fixture.create(byte[].class));

        lenient().when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any())).thenReturn(fixture.create(VaccinationCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any())).thenReturn(fixture.create(VaccinationCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificateQrCode(any())).thenReturn(fixture.create(TestCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any())).thenReturn(fixture.create(TestCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any())).thenReturn(fixture.create(RecoveryCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any())).thenReturn(fixture.create(RecoveryCertificatePdf.class));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        lenient().when(objectMapper.writer()).thenReturn(objectWriter);
        lenient().when(objectWriter.writeValueAsString(any())).thenReturn(fixture.create(String.class));
    }

    @Nested
    class GenerateVaccinationFromExistingCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationCertificatePdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
            verify(covidCertificatePdfGenerateRequestDtoMapperService).toVaccinationCertificatePdf(pdfGenerateRequestDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationCertificatePdfThrowsCreateCertificateException() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.generateFromExistingCovidCertificate(pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var vaccinationPdf = fixture.create(VaccinationCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(any())).thenReturn(vaccinationPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(vaccinationPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

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
        void shouldReturnBarcode() throws IOException, BarcodeException {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getUvci());
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
                    () -> service.generateFromExistingCovidCertificate(pdfGenerateRequestDto)
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
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

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
        void shouldReturnBarcode() throws IOException, BarcodeException {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getUvci());
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
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.generateFromExistingCovidCertificate(pdfGenerateRequestDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreatePdf_withCorrectPdfData() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var recoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            when(covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(any())).thenReturn(recoveryPdf);

            service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            verify(pdfCertificateGenerationService).generateCovidCertificate(eq(recoveryPdf), any(), any());
        }

        @Test
        void shouldCreatePdf_withCorrectBarcode() throws BarcodeException {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

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
            var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertArrayEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldReturnUVCI() {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var actual = service.generateFromExistingCovidCertificate(pdfGenerateRequestDto);

            assertNotNull(actual.getUvci());
        }
    }
    
    @Nested
    class GenerateVaccinationCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationCertificateQrCode() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToVaccinationCertificatePdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto)).thenReturn(qrCodeData);
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getVaccinationSigningInformation(createDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccination_ifMapDtoToVaccinationCertificatePdfThrowsCreateCertificateException() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation));
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var vaccinationPdf = fixture.create(VaccinationCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any())).thenReturn(vaccinationPdf);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(vaccinationPdf, barcode.getPayload(), now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldReturnUVCI() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdfByteArray);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getVaccinationInfo().get(0).getIdentifier(), createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(certificatePrintRequestDto);
            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateTestCovidCertificate {
        @Test
        void shouldMapDtoToTestCertificateQrCode() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toTestCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToTestCertificatePdf() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(createDto)).thenReturn(qrCodeData);
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toTestCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getTestSigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToTestCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifMapDtoToTestCertificatePdfThrowsCreateCertificateException() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation));
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var TestPdf = fixture.create(TestCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any())).thenReturn(TestPdf);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(TestPdf, barcode.getPayload(), LocalDateTime.now());
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldReturnUVCI() throws IOException {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdfByteArray);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }
    }

    @Nested
    class GenerateRecoveryCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificateQrCode() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToRecoveryCertificatePdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto)).thenReturn(qrCodeData);
            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");;
            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getRecoverySigningInformation(createDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfRecovery_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation));
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var RecoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any())).thenReturn(RecoveryPdf);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(RecoveryPdf, barcode.getPayload(), now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getPdf());
        }

        @Test
        void shouldUVCI() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto("de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto("de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any())).thenReturn(barcode);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto("de", "BITBITBIT");
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdfByteArray);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto("de");

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(certificatePrintRequestDto);
            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getRecoveryCertificateCreateDto("de", "BITBITBIT");

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    LocalDateTime getLocalDateTimeFromEpochMillis(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).toLocalDateTime();
    }
}
