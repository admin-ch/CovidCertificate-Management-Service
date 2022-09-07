package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.TestCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCertificatePdfMapper {

    public static TestCertificatePdf toTestCertificatePdf(
            TestCertificateCreateDto testCertificateCreateDto,
            IssuableTestDto issuableTestDto,
            TestCertificateQrCode qrCodeData,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new TestCertificatePdf(
                testCertificateCreateDto.getPersonData().getName().getFamilyName(),
                testCertificateCreateDto.getPersonData().getName().getGivenName(),
                testCertificateCreateDto.getPersonData().getDateOfBirth(),
                testCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                issuableTestDto.getTestType().typeDisplay,
                issuableTestDto.getDisplay(),
                testCertificateCreateDto.getTestInfo().get(0).getSampleDateTime(),
                TestResult.NEGATIVE.display,
                testCertificateCreateDto.getTestInfo().get(0).getTestingCentreOrFacility(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                qrCodeData.getTestInfo().get(0).getIdentifier());
    }
}
