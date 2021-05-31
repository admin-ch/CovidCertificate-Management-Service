package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.NegativeTestResult;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCertificatePdfMapper {

    public static TestCertificatePdf toTestCertificatePdf(
            TestCertificateCreateDto testCertificateCreateDto,
            TestValueSet testValueSet,
            TestCertificateQrCode qrCodeData,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new TestCertificatePdf(
                testCertificateCreateDto.getPersonData(),
                testCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                testValueSet.getType(),
                testValueSet.getName(),
                testValueSet.getManufacturer(),
                testCertificateCreateDto.getTestInfo().get(0).getSampleDateTime(),
                NegativeTestResult.DISPLAY,
                testCertificateCreateDto.getTestInfo().get(0).getTestingCentreOrFacility(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                qrCodeData.getTestInfo().get(0).getIdentifier());
    }
}
