package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateQrCode;
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

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;

import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getExceptionalCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryRatCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateCreateDto;
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
    private final JFixture fixture = new JFixture();
    @InjectMocks
    private CovidCertificateGenerationService service;
    @Mock
    private BarcodeService barcodeService;
    @Mock
    private PdfCertificateGenerationService pdfCertificateGenerationService;
    @Mock
    private CovidCertificateDtoMapperService covidCertificateDtoMapperService;
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
        lenient().when(covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(any()))
                 .thenReturn(fixture.create(VaccinationTouristCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toVaccinationTouristCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificateQrCode(any()))
                 .thenReturn(fixture.create(TestCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(TestCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any()))
                 .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(RecoveryCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(any()))
                 .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toRecoveryRatCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(RecoveryCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toAntibodyCertificateQrCode(any()))
                 .thenReturn(fixture.create(AntibodyCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toAntibodyCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(AntibodyCertificatePdf.class));
        lenient().when(covidCertificateDtoMapperService.toExceptionalCertificateQrCode(any()))
                 .thenReturn(fixture.create(ExceptionalCertificateQrCode.class));
        lenient().when(covidCertificateDtoMapperService.toExceptionalCertificatePdf(any(), any()))
                 .thenReturn(fixture.create(ExceptionalCertificatePdf.class));

        lenient().when(coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS))
                 .thenReturn(fixture.create(Instant.class));
        lenient().when(coseTime.calculateExpirationInstantPlusDays(Constants.EXPIRATION_PERIOD_30_DAYS))
                 .thenReturn(fixture.create(Instant.class));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        lenient().when(objectMapper.writer()).thenReturn(objectWriter);
        lenient().when(objectWriter.writeValueAsString(any())).thenReturn(fixture.create(String.class));
    }

    @Nested
    class GenerateVaccinationCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationCertificateQrCode() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToVaccinationCertificatePdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getVaccinationSigningInformation(createDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccination_ifMapDtoToVaccinationCertificatePdfThrowsCreateCertificateException() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

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
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var vaccinationPdf = fixture.create(VaccinationCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toVaccinationCertificatePdf(any(), any())).thenReturn(vaccinationPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(vaccinationPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() throws IOException {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(), inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toVaccinationCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getVaccinationInfo().get(0).getIdentifier(),
                                                  createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de",
                    "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationSigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateVaccinationTouristCovidCertificate {
        @Test
        void shouldMapDtoToVaccinationTouristCertificateQrCode() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationTouristCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToVaccinationTouristCertificatePdf() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(createDto)).thenReturn(
                    qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toVaccinationTouristCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getVaccinationTouristSigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToVaccinationTouristCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccinationTourist_ifMapDtoToVaccinationTouristCertificatePdfThrowsCreateCertificateException() {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(createDto)).thenReturn(
                    qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var vaccinationPdf = fixture.create(VaccinationTouristCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificatePdf(any(), any())).thenReturn(
                    vaccinationPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(vaccinationPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() throws IOException {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(), inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor = ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto(
                    "EU/1/20/1507",
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getVaccinationTouristInfo().get(0).getIdentifier(),
                                                  createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getVaccinationTouristCertificateCreateDto(
                    "EU/1/20/1507",
                    "de",
                    "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getVaccinationTouristSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateTestCovidCertificate {
        @Test
        void shouldMapDtoToTestCertificateQrCode() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toTestCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToTestCertificatePdf() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toTestCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getTestSigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToTestCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifMapDtoToTestCertificatePdfThrowsCreateCertificateException() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toTestCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var TestPdf = fixture.create(TestCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toTestCertificatePdf(any(), any())).thenReturn(TestPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(TestPdf, barcode.getPayload(),
                                                                                 LocalDateTime.now());
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var pdf = fixture.create(byte[].class);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldReturnUVCI() throws IOException {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
            assertNotNull(uvciArgumentCaptor.getValue());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getTestSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }
    }

    @Nested
    class GenerateRecoveryCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryCertificateQrCode() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToRecoveryCertificatePdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getRecoverySigningInformation(createDto);
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfRecovery_ifMapDtoToRecoveryCertificatePdfThrowsCreateCertificateException() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

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
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var RecoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toRecoveryCertificatePdf(any(), any())).thenReturn(RecoveryPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(RecoveryPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var pdf = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldUVCI() throws IOException {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

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
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getRecoveryCertificateCreateDto("de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getRecoveryCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoverySigningInformation(any())).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateRecoveryRatCovidCertificate {
        @Test
        void shouldMapDtoToRecoveryRatCertificateQrCode() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryRatCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToRecoveryRatCertificatePdf() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toRecoveryRatCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getRecoveryRatSigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToRecoveryRatCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfRecoveryRat_ifMapDtoToRecoveryRatCertificatePdfThrowsCreateCertificateException() {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toRecoveryRatCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var RecoveryPdf = fixture.create(RecoveryCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toRecoveryRatCertificatePdf(any(), any())).thenReturn(RecoveryPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(RecoveryPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var pdf = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldUVCI() throws IOException {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getRecoveryRatCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getRecoveryRatCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getRecoveryRatCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getRecoveryRatCertificateCreateDto(
                    "de",
                    "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getRecoveryRatSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateAntibodyCovidCertificate {
        @Test
        void shouldMapDtoToAntibodyCertificateQrCode() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toAntibodyCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToAntibodyCertificatePdf() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var qrCodeData = fixture.create(AntibodyCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toAntibodyCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toAntibodyCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getAntibodySigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToAntibodyCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getAntibodyCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toAntibodyCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfAntibody_ifMapDtoToAntibodyCertificatePdfThrowsCreateCertificateException() {
            var createDto = getAntibodyCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toAntibodyCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var qrCodeData = fixture.create(AntibodyCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toAntibodyCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var AntibodyPdf = fixture.create(AntibodyCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toAntibodyCertificatePdf(any(), any())).thenReturn(AntibodyPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(AntibodyPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var pdf = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldUVCI() throws IOException {
            var createDto = getAntibodyCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getAntibodyCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getAntibodyCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getAntibodyCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getAntibodyCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getAntibodyCertificateCreateDto("de");
            var qrCodeData = fixture.create(AntibodyCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toAntibodyCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getAntibodyInfo().get(0).getIdentifier(), createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getAntibodyCertificateCreateDto("de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getAntibodyCertificateCreateDto("de", "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getAntibodySigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }

    @Nested
    class GenerateExceptionalCovidCertificate {
        @Test
        void shouldMapDtoToExceptionalCertificateQrCode() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toExceptionalCertificateQrCode(createDto);
        }

        @Test
        void shouldMapDtoToExceptionalCertificatePdf() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var qrCodeData = fixture.create(ExceptionalCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toExceptionalCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(covidCertificateDtoMapperService).toExceptionalCertificatePdf(createDto, qrCodeData);
        }

        @Test
        void shouldGetSigningInformationForTheCovidCertificate() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);
            verify(signingInformationService).getExceptionalSigningInformation();
        }

        @Test
        void throwsCreateCertificateException_ifMapDtoToExceptionalCertificateQrCodeThrowsCreateCertificateException() {
            var createDto = getExceptionalCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toExceptionalCertificateQrCode(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfExceptional_ifMapDtoToExceptionalCertificatePdfThrowsCreateCertificateException() {
            var createDto = getExceptionalCertificateCreateDto("de");
            var expected = fixture.create(CreateCertificateException.class);
            when(covidCertificateDtoMapperService.toExceptionalCertificatePdf(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                                () -> service.generateCovidCertificate(createDto));

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldCreateBarcode_withCorrectContents() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var qrCodeData = fixture.create(ExceptionalCertificateQrCode.class);
            when(covidCertificateDtoMapperService.toExceptionalCertificateQrCode(createDto)).thenReturn(qrCodeData);
            var contents = fixture.create(String.class);
            var objectWriter = mock(ObjectWriter.class);
            when(objectMapper.writer()).thenReturn(objectWriter);
            when(objectWriter.writeValueAsString(qrCodeData)).thenReturn(contents);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(eq(contents), any(), any());
        }

        @Test
        void shouldCreateBarcode_usingCorrectSigningInformation() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            service.generateCovidCertificate(createDto);

            verify(barcodeService).createBarcode(any(), eq(signingInformation), any());
        }

        @Test
        void shouldCreatePdf() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var ExceptionalPdf = fixture.create(ExceptionalCertificatePdf.class);
            var barcode = fixture.create(Barcode.class);
            var now = LocalDateTime.now();
            when(covidCertificateDtoMapperService.toExceptionalCertificatePdf(any(), any())).thenReturn(ExceptionalPdf);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

                service.generateCovidCertificate(createDto);

                verify(pdfCertificateGenerationService).generateCovidCertificate(ExceptionalPdf, barcode.getPayload(),
                                                                                 now);
            }
        }

        @Test
        void shouldReturnBarcode() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(barcode.getImage(), actual.getResponseDto().getQrCode());
        }

        @Test
        void shouldReturnPdf() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var pdf = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);

            var actual = service.generateCovidCertificate(createDto);

            assertEquals(pdf, actual.getResponseDto().getPdf());
        }

        @Test
        void shouldUVCI() throws IOException {
            var createDto = getExceptionalCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            var actual = service.generateCovidCertificate(createDto);

            assertNotNull(actual.getResponseDto().getUvci());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectAppCode_whenAppCodeIsPassed() {
            var createDto = getExceptionalCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(createDto.getAppCode(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getCode());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectHCert_whenAppCodeIsPassed() {
            var createDto = getExceptionalCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var barcode = fixture.create(Barcode.class);
            when(barcodeService.createBarcode(any(), any(), any())).thenReturn(barcode);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(barcode.getPayload(), inAppDeliveryRequestDtoArgumentCaptor.getValue().getHcert());
        }

        @Test
        void shouldSendInAppDelivery_withCorrectPdfData_whenAppCodeIsPassed() {
            var createDto = getExceptionalCertificateCreateDto("de", "BITBITBIT");
            var uvciArgumentCaptor = ArgumentCaptor.forClass(String.class);
            var inAppDeliveryRequestDtoArgumentCaptor =
                    ArgumentCaptor.forClass(InAppDeliveryRequestDto.class);
            var pdfByteArray = fixture.create(byte[].class);
            var pdf = Base64.getEncoder().encodeToString(pdfByteArray);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(
                    pdfByteArray);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));

            verify(inAppDeliveryClient, times(1)).deliverToApp(
                    uvciArgumentCaptor.capture(),
                    inAppDeliveryRequestDtoArgumentCaptor.capture());
            assertNotNull(uvciArgumentCaptor.getValue());
            assertEquals(pdf, inAppDeliveryRequestDtoArgumentCaptor.getValue().getPdf());
        }

        @Test
        void shouldNotSendInAppDelivery_whenNoAppCodeIsPassed() {
            var createDto = getExceptionalCertificateCreateDto("de");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(inAppDeliveryClient);
        }

        @Test
        void shouldMapCreateDto_toCertificatePrintRequestDto_whenAddressPassed() {
            var createDto = getExceptionalCertificateCreateDto("de");
            var qrCodeData = fixture.create(ExceptionalCertificateQrCode.class);
            var pdf = fixture.create(byte[].class);
            when(covidCertificateDtoMapperService.toExceptionalCertificateQrCode(any())).thenReturn(qrCodeData);
            when(pdfCertificateGenerationService.generateCovidCertificate(any(), any(), any())).thenReturn(pdf);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(certificatePrintRequestDtoMapper, times(1))
                    .toCertificatePrintRequestDto(pdf, qrCodeData.getExceptionalInfo().get(0).getIdentifier(),
                                                  createDto);
        }

        @Test
        void shouldCallPrintingService_whenAddressPassed() {
            var createDto = getExceptionalCertificateCreateDto("de");
            var certificatePrintRequestDto = fixture.create(CertificatePrintRequestDto.class);
            when(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(any(), any(), any())).thenReturn(
                    certificatePrintRequestDto);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verify(printQueueClient, times(1)).sendPrintJob(certificatePrintRequestDto);
        }

        @Test
        void shouldNotCallPrintingService_whenNoAddressPassed() {
            var createDto = getExceptionalCertificateCreateDto("de", "BITBITBIT");
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationService.getExceptionalSigningInformation()).thenReturn(signingInformation);

            assertDoesNotThrow(() -> service.generateCovidCertificate(createDto));
            verifyNoInteractions(printQueueClient);
            verifyNoInteractions(certificatePrintRequestDtoMapper);
        }
    }
}
