package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.NegativeTestResult;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateData;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCertificateQrCodeMapper {

    public static TestCertificateQrCode toTestCertificateQrCode(
            TestCertificateCreateDto testCertificateCreateDto,
            TestValueSet testValueSet
    ) {
        return new TestCertificateQrCode(
                VERSION,
                CovidCertificatePersonMapper.toCovidCertificatePerson(testCertificateCreateDto.getPersonData()),
                TestCertificateQrCodeMapper.toTestCertificateDataList(
                        testCertificateCreateDto.getTestInfo(),
                        testValueSet
                )
        );
    }

    private static List<TestCertificateData> toTestCertificateDataList(
            List<TestCertificateDataDto> testCertificateDataDtoList,
            TestValueSet testValueSet
    ) {
        return testCertificateDataDtoList.stream().map(testCertificateDataDto ->
                toTestCertificateData(testCertificateDataDto, testValueSet)
        ).collect(Collectors.toList());
    }

    private static TestCertificateData toTestCertificateData(
            TestCertificateDataDto testCertificateDataDto,
            TestValueSet testValueSet) {
        return new TestCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                testValueSet.getTypeCode(),
                testValueSet.getName(),
                testValueSet.getManufacturerCodeEu(),
                testCertificateDataDto.getSampleDateTime(),
                NegativeTestResult.CODE,
                testCertificateDataDto.getTestingCentreOrFacility(),
                testCertificateDataDto.getMemberStateOfTest(),
                ISSUER,
                UVCI.generateUVCI(testCertificateDataDto.toString())
        );
    }
}
