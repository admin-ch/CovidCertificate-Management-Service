package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.CsvException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CsvResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvServiceTest {
    @InjectMocks
    private CsvService service;

    @Mock
    private CovidCertificateGenerationService covidCertificateGenerationService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;
    @Mock
    private KpiDataService kpiLogService;
    @Mock
    private ValueSetsService valueSetsService;
    @Mock
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    private final JFixture fixture = new JFixture();
    private final File validRecoveryFile;
    private final File validTestFile;
    private final File invalidTestFile;
    private final File validVaccinationFile;
    private final File emptyCsv;
    private final File invalidCsv;
    private final File invalidMultipleCsv;
    private final File validMultipleCsv;

    public CsvServiceTest() {
        validRecoveryFile = new File("src/test/resources/csv/recovery_csv_valid.csv");
        validTestFile = new File("src/test/resources/csv/test_csv_valid.csv");
        invalidTestFile = new File("src/test/resources/csv/test_csv_adress_invalid.csv");
        validVaccinationFile = new File("src/test/resources/csv/vaccination_csv_valid.csv");
        emptyCsv = new File("src/test/resources/csv/recovery_csv_empty.csv");
        invalidCsv = new File("src/test/resources/csv/recovery_csv_invalid.csv");
        invalidMultipleCsv = new File("src/test/resources/csv/vaccination_csv_multiple_invalid.csv");
        validMultipleCsv = new File("src/test/resources/csv/vaccination_csv_multiple_valid.csv");
    }

    @BeforeEach
    void setUp() throws IOException {
        lenient().when(valueSetsService.getCountryCode(anyString(), anyString())).thenReturn(fixture.create(CountryCode.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Test
    void testInvalidCertificateType() {
        var file = Mockito.mock(MultipartFile.class);
        var exception = assertThrows(CreateCertificateException.class,
                () -> service.handleCsvRequest(file, "blub"));
        assertEquals(INVALID_CERTIFICATE_TYPE, exception.getError());
    }

    @Test
    void testEmptyCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(emptyCsv);
        var inputStream2 = new FileInputStream(emptyCsv);
        var inputStream3 = new FileInputStream(emptyCsv);
        when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);
        var recoveryName = CertificateType.recovery.name();
        var exception = assertThrows(CreateCertificateException.class,
                () -> service.handleCsvRequest(file, recoveryName));
        assertEquals(INVALID_CSV_SIZE, exception.getError());
    }

    @Test
    void testInvalidCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(invalidCsv);
        var inputStream2 = new FileInputStream(invalidCsv);
        var inputStream3 = new FileInputStream(invalidCsv);
        when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);
        var exception = assertThrows(CsvException.class,
                () -> service.handleCsvRequest(file, CertificateType.recovery.name()));
        assertEquals(INVALID_CREATE_REQUESTS.getErrorCode(), exception.getError().getErrorCode());
    }

    @Test
    void testMultipleInvalidCsv() throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(invalidMultipleCsv);
        var inputStream2 = new FileInputStream(invalidMultipleCsv);
        var inputStream3 = new FileInputStream(invalidMultipleCsv);
        when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);
        var vaccinationNamme = CertificateType.vaccination.name();
        var exception = assertThrows(CsvException.class,
                () -> service.handleCsvRequest(file, vaccinationNamme));
        assertEquals(INVALID_CREATE_REQUESTS.getErrorCode(), exception.getError().getErrorCode());
    }

    private static class CertificateCreateDtoFamilyNameMatcher<T extends CertificateCreateDto> implements ArgumentMatcher<T> {
        private final String familyName;

        private CertificateCreateDtoFamilyNameMatcher(String familyName) {
            this.familyName = familyName;
        }

        @Override
        public boolean matches(T t) {
            if (t == null) return false;
            var actual = t.getPersonData().getName().getFamilyName();
            return familyName.equals(actual);
        }

        public String toString() {
            return String.format("with familyName=%s", familyName);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"src/test/resources/csv/recovery_ansi.csv",
            "src/test/resources/csv/recovery_utf8.csv"})
    void testEncoding(String path) throws IOException {
        var expectedFamilyName = "MüllerTest";
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(path);
        var inputStream2 = new FileInputStream(path);
        var inputStream3 = new FileInputStream(path);
        when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

        service.handleCsvRequest(file, CertificateType.recovery.name());

        verify(covidCertificateGenerationService).generateCovidCertificate(argThat(new CertificateCreateDtoFamilyNameMatcher<RecoveryCertificateCreateDto>(expectedFamilyName)));

        inputStream.close();
        inputStream2.close();
    }

    @Nested
    class GenerateRecoveryCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validRecoveryFile);
            var inputStream2 = new FileInputStream(validRecoveryFile);
            var inputStream3 = new FileInputStream(validRecoveryFile);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.recovery.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

        @ParameterizedTest
        @ValueSource(strings = {"src/test/resources/csv/recovery_ansi.csv",
                "src/test/resources/csv/recovery_print_ansi.csv",
                "src/test/resources/csv/recovery_print_utf8.csv",
                "src/test/resources/csv/recovery_utf8.csv",
                "src/test/resources/csv/recovery_ansi_tab.csv"})
        void massTest(String path) throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(path);
            var inputStream2 = new FileInputStream(path);
            var inputStream3 = new FileInputStream(path);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.recovery.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }
    }

    @Nested
    class GenerateTestCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validTestFile);
            var inputStream2 = new FileInputStream(validTestFile);
            var inputStream3 = new FileInputStream(validTestFile);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.test.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

        @Test
        void invalid_with_adress() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(invalidTestFile);
            var inputStream2 = new FileInputStream(invalidTestFile);
            var inputStream3 = new FileInputStream(invalidTestFile);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);
            var testName = CertificateType.test.name();
            var exception = assertThrows(CsvException.class,
                    () -> service.handleCsvRequest(file, testName));
            assertEquals(INVALID_CREATE_REQUESTS.getErrorCode(), exception.getError().getErrorCode());
        }

        @ParameterizedTest
        @ValueSource(strings = {"src/test/resources/csv/test_ansi.csv",
                "src/test/resources/csv/test_utf8.csv",
                "src/test/resources/csv/test_utf8_bom.csv"})
        void massTest(String path) throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(path);
            var inputStream2 = new FileInputStream(path);
            var inputStream3 = new FileInputStream(path);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.test.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }
    }

    @Nested
    class GenerateVaccinationCertificateCsv {
        @Test
        void successful() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validVaccinationFile);
            var inputStream2 = new FileInputStream(validVaccinationFile);
            var inputStream3 = new FileInputStream(validVaccinationFile);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.vaccination.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

        @Test
        void successfulMultiple() throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(validMultipleCsv);
            var inputStream2 = new FileInputStream(validMultipleCsv);
            var inputStream3 = new FileInputStream(validMultipleCsv);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.vaccination.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

        @ParameterizedTest
        @ValueSource(strings = {"src/test/resources/csv/vaccination_ansi.csv",
                "src/test/resources/csv/vaccination_print_ansi.csv",
                "src/test/resources/csv/vaccination_print_utf8.csv",
                "src/test/resources/csv/vaccination_utf8.csv"})
        void massTest(String path) throws IOException {
            var file = Mockito.mock(MultipartFile.class);
            var inputStream = new FileInputStream(path);
            var inputStream2 = new FileInputStream(path);
            var inputStream3 = new FileInputStream(path);
            when(file.getInputStream()).thenReturn(inputStream, inputStream2, inputStream3);

            CsvResponseDto response = service.handleCsvRequest(file, CertificateType.vaccination.name());
            assertNotNull(response.getZip());
            inputStream.close();
        }

    }
}
