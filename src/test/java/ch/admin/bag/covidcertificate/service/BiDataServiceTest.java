package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.BiDataException;
import ch.admin.bag.covidcertificate.api.response.BiDataResponseDto;
import ch.admin.bag.covidcertificate.domain.BiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import com.flextrade.jfixture.JFixture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class BiDataServiceTest {

    private final JFixture fixture = new JFixture();

    @InjectMocks
    private BiDataService biDataService;

    @Mock
    private KpiDataRepository kpiDataRepository;

    @Test
    void loadBiData_with_a_week_as_fromDate_and_toDate_should_succeed() {
        // with data
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 16);
        List<BiData> searchResult = this.createResultList(from, to);
        when(kpiDataRepository.findAllByDateRange(any(), any()))
                .thenReturn(searchResult);

        // do test
        BiDataResponseDto response = this.biDataService.loadBiData(from, to);

        // check result
        assertThat(response).isNotNull();
        assertThat(response.getZip()).isNotEmpty();
    }

    @Test
    void loadBiData_with_fromDate_null_should_fail() {
        LocalDate from = null;
        LocalDate to = LocalDate.of(2022, 10, 18);
        BiDataException biDataException = assertThrows(BiDataException.class, () -> this.biDataService.loadBiData(from, to));
        assertThat(biDataException).isNotNull();
        assertThat(biDataException.getBiDataError().getErrorMessage()).isEqualTo(
                "The given dates sent to request BI data are not valid. Please define a week or a month e.g. from 2022-10-10 to 2022-10-16.");
        assertThat(biDataException.getBiDataError().getErrorCode()).isEqualTo(1101);
        assertThat(biDataException.getBiDataError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void loadBiData_with_toDate_null_should_fail() {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = null;
        BiDataException biDataException = assertThrows(BiDataException.class, () -> this.biDataService.loadBiData(from, to));
        assertThat(biDataException).isNotNull();
        assertThat(biDataException.getBiDataError().getErrorMessage()).isEqualTo(
                "The given dates sent to request BI data are not valid. Please define a week or a month e.g. from 2022-10-10 to 2022-10-16.");
        assertThat(biDataException.getBiDataError().getErrorCode()).isEqualTo(1101);
        assertThat(biDataException.getBiDataError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void loadBiData_with_more_than_a_week_as_fromDate_and_toDate_should_fail() {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 18);
        BiDataException biDataException = assertThrows(BiDataException.class, () -> this.biDataService.loadBiData(from, to));
        assertThat(biDataException).isNotNull();
        assertThat(biDataException.getBiDataError().getErrorMessage()).isEqualTo(
                "The given dates sent to request BI data are not valid. Please define a week or a month e.g. from 2022-10-10 to 2022-10-16.");
        assertThat(biDataException.getBiDataError().getErrorCode()).isEqualTo(1101);
        assertThat(biDataException.getBiDataError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void loadBiData_with_less_than_a_week_as_fromDate_and_toDate_should_fail() {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 14);
        BiDataException biDataException = assertThrows(BiDataException.class, () -> this.biDataService.loadBiData(from, to));
        assertThat(biDataException).isNotNull();
        assertThat(biDataException.getBiDataError().getErrorMessage()).isEqualTo(
                "The given dates sent to request BI data are not valid. Please define a week or a month e.g. from 2022-10-10 to 2022-10-16.");
        assertThat(biDataException.getBiDataError().getErrorCode()).isEqualTo(1101);
        assertThat(biDataException.getBiDataError().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void loadBiData_with_month_November_as_fromDate_and_toDate_should_succeed() {
        // with data
        LocalDate from = LocalDate.of(2022, 11, 1);
        LocalDate to = LocalDate.of(2022, 11, 30);
        List<BiData> searchResult = this.createResultList(from, to);
        when(kpiDataRepository.findAllByDateRange(any(), any()))
                .thenReturn(searchResult);

        // do test
        BiDataResponseDto response = this.biDataService.loadBiData(from, to);

        // check result
        assertThat(response).isNotNull();
        assertThat(response.getZip()).isNotEmpty();
    }

    @Test
    void loadBiData_with_month_December_as_fromDate_and_toDate_should_succeed() {
        // with data
        LocalDate from = LocalDate.of(2022, 12, 1);
        LocalDate to = LocalDate.of(2022, 12, 31);
        List<BiData> searchResult = this.createResultList(from, to);
        when(kpiDataRepository.findAllByDateRange(any(), any()))
                .thenReturn(searchResult);

        // do test
        BiDataResponseDto response = this.biDataService.loadBiData(from, to);

        // check result
        assertThat(response).isNotNull();
        assertThat(response.getZip()).isNotEmpty();
    }

    @Test
    void loadBiData_with_month_February_as_fromDate_and_toDate_should_succeed() {
        // with data
        LocalDate from = LocalDate.of(2022, 2, 1);
        LocalDate to = LocalDate.of(2022, 2, 28);
        List<BiData> searchResult = this.createResultList(from, to);
        when(kpiDataRepository.findAllByDateRange(any(), any()))
                .thenReturn(searchResult);

        // do test
        BiDataResponseDto response = this.biDataService.loadBiData(from, to);

        // check result
        assertThat(response).isNotNull();
        assertThat(response.getZip()).isNotEmpty();
    }

    @Test
    void loadBiData_with_month_February_of_leap_year_as_fromDate_and_toDate_should_succeed() {
        // with data
        LocalDate from = LocalDate.of(2020, 2, 1);
        LocalDate to = LocalDate.of(2020, 2, 29);
        List<BiData> searchResult = this.createResultList(from, to);
        when(kpiDataRepository.findAllByDateRange(any(), any()))
                .thenReturn(searchResult);

        // do test
        BiDataResponseDto response = this.biDataService.loadBiData(from, to);

        // check result
        assertThat(response).isNotNull();
        assertThat(response.getZip()).isNotEmpty();
    }

    private List<BiData> createResultList(LocalDate fromDate, LocalDate toDate) {
        List<BiData> result = new ArrayList<>();
        long diff = ChronoUnit.DAYS.between(fromDate, toDate);
        for(long index = 0; index <= diff; index ++) {
            BiDataMock mock = fixture.create(BiDataMock.class);
            mock.setTimestamp(fromDate.plusDays(index).atTime(LocalTime.NOON));
            result.add(mock);
        }
        return result;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class BiDataMock implements BiData {
        public UUID id;
        public LocalDateTime timestamp;
        public String type;
        public String value;
        public String details;
        public String country;
        public String systemSource;
        public String apiGatewayId;
        public String inAppDeliveryCode;
        public Boolean fraud;
        public String keyIdentifier;
    }
}
