package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificateData;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificateQrCodeMapper {

    public static RecoveryRatCertificateQrCode toRecoveryRatCertificateQrCode(
            RecoveryRatCertificateCreateDto recoveryRatCertificateCreateDto,
            IssuableTestDto testValueSet
    ) {
        return new RecoveryRatCertificateQrCode(
                VERSION,
                CovidCertificatePersonMapper.toCovidCertificatePerson(recoveryRatCertificateCreateDto.getPersonData()),
                toRecoveryRatCertificateDataList(
                        recoveryRatCertificateCreateDto.getTestInfo(),
                        testValueSet
                )
        );
    }

    private static List<RecoveryRatCertificateData> toRecoveryRatCertificateDataList(
            List<RecoveryRatCertificateDataDto> recoveryRatCertificateDataDtoList,
            IssuableTestDto issuableTestRatDto
    ) {
        return recoveryRatCertificateDataDtoList.stream().map(recoveryRatCertificateDataDto ->
                toRecoveryRatCertificateData(recoveryRatCertificateDataDto, issuableTestRatDto)
        ).collect(Collectors.toList());
    }

    private static RecoveryRatCertificateData toRecoveryRatCertificateData(
            RecoveryRatCertificateDataDto recoveryRatCertificateDataDto,
            IssuableTestDto issuableTestDto) {
        return new RecoveryRatCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                issuableTestDto.getTestType().typeCode,
                issuableTestDto.getCode(),
                recoveryRatCertificateDataDto.getSampleDateTime().truncatedTo(ChronoUnit.SECONDS),
                TestResult.POSITIVE.code,
                recoveryRatCertificateDataDto.getTestingCentreOrFacility(),
                recoveryRatCertificateDataDto.getMemberStateOfTest(),
                ISSUER,
                UVCI.generateUVCI(recoveryRatCertificateDataDto.toString())
        );
    }
}
