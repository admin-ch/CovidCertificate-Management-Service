package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.NegativeTestResult;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCertificatePdfGenerateRequestDtoMapper {

    public static TestCertificatePdf toTestCertificatePdf(
            TestCertificatePdfGenerateRequestDto testCertificateCreateDto,
            TestValueSet testValueSet,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new TestCertificatePdf(
                testCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                testCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                testCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                testCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                testValueSet.getType(),
                testValueSet.getName(),
                testValueSet.getManufacturer(),
                testCertificateCreateDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime(),
                NegativeTestResult.DISPLAY,
                testCertificateCreateDto.getDecodedCert().getTestInfo().get(0).getTestingCentreOrFacility(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                testCertificateCreateDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }
}
