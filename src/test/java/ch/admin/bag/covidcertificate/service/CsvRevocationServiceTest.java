package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CsvRevocationServiceTest {
    private final JFixture fixture = new JFixture();

    @InjectMocks
    private CsvRevocationService service;
    @Mock
    private FileService fileService;
    @Mock
    private RevocationService revocationService;

    @BeforeEach
    void setUp() throws IOException {
        Mockito.clearInvocations(revocationService);

        lenient().when(revocationService.performMassRevocation(any(RevocationListDto.class)))
                .thenReturn(fixture.create(RevocationListResponseDto.class));
        lenient().when(fileService.getSeparator(any(MultipartFile.class)))
                .thenReturn(';');
    }

    @ParameterizedTest
    @ValueSource(strings = {"src/test/resources/csv/revocation/empty.csv"})
    void emptyCsvShouldReturnError(String path) throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        var inputStream = new FileInputStream(path);
        when(file.getInputStream()).thenReturn(inputStream);
        var exception = assertThrows(RevocationException.class,
                () -> service.handleCsvRequest(file)
        );
        assertEquals(INVALID_CSV_SIZE.getErrorCode(), exception.getError().getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"src/test/resources/csv/revocation/invalid_1.csv"})
    void invalidFileShouldStillCallService(String path) throws Exception {
        var file = Mockito.mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new FileInputStream(path), new FileInputStream(path), new FileInputStream(path));

        service.handleCsvRequest(file);

        verify(revocationService).performMassRevocation(argThat(revocationListDto -> revocationListDto.getUvcis().size() == 1));
    }
}
