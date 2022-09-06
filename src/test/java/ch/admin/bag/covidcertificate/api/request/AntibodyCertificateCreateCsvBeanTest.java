package ch.admin.bag.covidcertificate.api.request;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AntibodyCertificateCreateCsvBeanTest {

    @Test
    void mapToCreateDto_validFields_dtoCreated() {
        AntibodyCertificateCsvBean antibodyCertificateCsvBean = new AntibodyCertificateCsvBean("2022-04-30", "Dr. Strange Praxis");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "familyName", "test");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "givenName", "test");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "dateOfBirth", "01.01.2000");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "language", "fr");
        assertNotNull(antibodyCertificateCsvBean.mapToCreateDto());
    }

    @Test
    void mapToCreateDto_nullFields_dtoCreated() {
        AntibodyCertificateCsvBean antibodyCertificateCsvBean = new AntibodyCertificateCsvBean("2022-04-30", null);
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "familyName", "test");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "givenName", "test");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "dateOfBirth", "01.01.2000");
        ReflectionTestUtils.setField(antibodyCertificateCsvBean, "language", "fr");
        assertNotNull(antibodyCertificateCsvBean.mapToCreateDto());
    }
}
