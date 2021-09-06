package ch.admin.bag.covidcertificate.api.request;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestCertificateCsvBeanTest {

    @Test
    void mapToCreateDto_validFields_dtoCreated(){
        TestCertificateCsvBean testCertificateCsvBean = new TestCertificateCsvBean("123", "123", "2021-06-01T11:34Z", "123", "CH");
        ReflectionTestUtils.setField(testCertificateCsvBean, "familyName", "test");
        ReflectionTestUtils.setField(testCertificateCsvBean, "givenName", "test");
        ReflectionTestUtils.setField(testCertificateCsvBean, "dateOfBirth", "01.01.2000");
        ReflectionTestUtils.setField(testCertificateCsvBean, "language", "fr");
        assertNotNull(testCertificateCsvBean.mapToCreateDto());
    }

    @Test
    void mapToCreateDto_nullFields_dtoCreated(){
        TestCertificateCsvBean testCertificateCsvBean = new TestCertificateCsvBean(null, null, "2021-06-01T11:34Z", null, null);
        ReflectionTestUtils.setField(testCertificateCsvBean, "familyName", "test");
        ReflectionTestUtils.setField(testCertificateCsvBean, "givenName", "test");
        ReflectionTestUtils.setField(testCertificateCsvBean, "dateOfBirth", "01.01.2000");
        ReflectionTestUtils.setField(testCertificateCsvBean, "language", "fr");
        assertNotNull(testCertificateCsvBean.mapToCreateDto());
    }


}
