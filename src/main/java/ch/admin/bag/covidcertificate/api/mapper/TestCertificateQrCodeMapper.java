package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateData;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCertificateQrCodeMapper {

    public static TestCertificateQrCode toTestCertificateQrCode(
            TestCertificateCreateDto testCertificateCreateDto,
            IssuableTestDto testValueSet
    ) {
        return new TestCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(testCertificateCreateDto.getPersonData()),
                TestCertificateQrCodeMapper.toTestCertificateDataList(
                        testCertificateCreateDto.getTestInfo(),
                        testValueSet
                )
        );
    }

    private static List<TestCertificateData> toTestCertificateDataList(
            List<TestCertificateDataDto> testCertificateDataDtoList,
            IssuableTestDto issuableTestDto
    ) {
        return testCertificateDataDtoList.stream().map(testCertificateDataDto ->
                toTestCertificateData(testCertificateDataDto, issuableTestDto)
        ).collect(Collectors.toList());
    }

    private static TestCertificateData toTestCertificateData(
            TestCertificateDataDto testCertificateDataDto,
            IssuableTestDto issuableTestDto) {
        return new TestCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                issuableTestDto.getTestType().typeCode,
                issuableTestDto.getCode(),
                testCertificateDataDto.getSampleDateTime().truncatedTo(ChronoUnit.SECONDS),
                TestResult.NEGATIVE.code,
                testCertificateDataDto.getTestingCentreOrFacility(),
                testCertificateDataDto.getMemberStateOfTest(),
                ISSUER,
                UVCI.generateUVCI(testCertificateDataDto.toString())
        );
    }
}
