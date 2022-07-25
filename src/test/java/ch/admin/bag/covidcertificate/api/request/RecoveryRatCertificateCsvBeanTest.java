package ch.admin.bag.covidcertificate.api.request;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;

class RecoveryRatCertificateCsvBeanTest {

    @Test
    void mapToCreateDto_sampleDateTime_without_time_and_zone() {
        ZonedDateTime sampleDate = LocalDate.now().atStartOfDay(SWISS_TIMEZONE);
        String sampleDateString = sampleDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        RecoveryRatCertificateCsvBean recoveryRatCertificateCsvBean = new RecoveryRatCertificateCsvBean(
                sampleDateString, "CH");
        setOtherFields(recoveryRatCertificateCsvBean);

        RecoveryRatCertificateCreateDto result = recoveryRatCertificateCsvBean.mapToCreateDto();

        doAssertions(sampleDate, result);
    }

    @Test
    void mapToCreateDto_sampleDateTime_with_time_and_zone_utc() {
        ZonedDateTime sampleDate = LocalDate.now().atStartOfDay(ZoneId.of("UTC"));
        String sampleDateString = sampleDate.format(ISO_INSTANT);
        RecoveryRatCertificateCsvBean recoveryRatCertificateCsvBean = new RecoveryRatCertificateCsvBean(
                sampleDateString, "CH");
        setOtherFields(recoveryRatCertificateCsvBean);

        RecoveryRatCertificateCreateDto result = recoveryRatCertificateCsvBean.mapToCreateDto();

        doAssertions(sampleDate, result);
    }

    @Test
    void mapToCreateDto_sampleDateTime_with_time_and_zone_zurich() {
        ZoneId zone_zurich = ZoneId.of("Europe/Zurich");
        ZonedDateTime sampleDate = LocalDate.now().atStartOfDay(zone_zurich);
        String sampleDateString = sampleDate.format(ISO_ZONED_DATE_TIME);
        RecoveryRatCertificateCsvBean recoveryRatCertificateCsvBean = new RecoveryRatCertificateCsvBean(
                sampleDateString, "CH");
        setOtherFields(recoveryRatCertificateCsvBean);

        RecoveryRatCertificateCreateDto result = recoveryRatCertificateCsvBean.mapToCreateDto();

        doAssertions(sampleDate, result);
    }

    @Test
    void mapToCreateDto_sampleDateTime_with_time_only() {
        ZonedDateTime sampleDate = LocalDate.now().atStartOfDay(SWISS_TIMEZONE);
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd")
                .appendLiteral("T")
                .appendPattern("HH:mm:ss")
                .toFormatter();
        String sampleDateString = sampleDate.format(dateTimeFormatter);
        RecoveryRatCertificateCsvBean recoveryRatCertificateCsvBean = new RecoveryRatCertificateCsvBean(
                sampleDateString, "CH");
        setOtherFields(recoveryRatCertificateCsvBean);

        RecoveryRatCertificateCreateDto result = recoveryRatCertificateCsvBean.mapToCreateDto();

        doAssertions(sampleDate, result);
    }

    private void doAssertions(ZonedDateTime sampleDate, RecoveryRatCertificateCreateDto result) {
        assertThat(result).isNotNull();
        assertThat(result.getTestInfo().size()).isEqualTo(1);
        RecoveryRatCertificateDataDto resultData = result.getTestInfo().get(0);
        assertThat(resultData).isNotNull();
        assertThat(resultData.getSampleDateTime()).isEqualTo(sampleDate);
    }

    private void setOtherFields(RecoveryRatCertificateCsvBean recoveryRatCertificateCsvBean) {
        ReflectionTestUtils.setField(recoveryRatCertificateCsvBean, "familyName", "test");
        ReflectionTestUtils.setField(recoveryRatCertificateCsvBean, "givenName", "test");
        ReflectionTestUtils.setField(recoveryRatCertificateCsvBean, "dateOfBirth", "01.01.2000");
        ReflectionTestUtils.setField(recoveryRatCertificateCsvBean, "language", "fr");
    }
}
