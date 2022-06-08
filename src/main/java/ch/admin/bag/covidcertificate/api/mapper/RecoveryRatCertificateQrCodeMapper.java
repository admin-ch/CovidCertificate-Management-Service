package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateDataDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateData;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.util.DateHelper;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificateQrCodeMapper {

    public static RecoveryCertificateQrCode toRecoveryCertificateQrCode(RecoveryRatCertificateCreateDto recoveryRatCertificateCreateDto) {
        return new RecoveryCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(recoveryRatCertificateCreateDto.getPersonData()),
                RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateDataList(
                        recoveryRatCertificateCreateDto.getTestInfo())
        );
    }

    private static List<RecoveryCertificateData> toRecoveryCertificateDataList(List<RecoveryRatCertificateDataDto> recoveryRatCertificateDataDtoList) {
        return recoveryRatCertificateDataDtoList.stream().map(
                RecoveryRatCertificateQrCodeMapper::toRecoveryCertificateData
        ).collect(Collectors.toList());
    }

    private static RecoveryCertificateData toRecoveryCertificateData(RecoveryRatCertificateDataDto recoveryRatCertificateDataDtoList) {
        return new RecoveryCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                recoveryRatCertificateDataDtoList.getSampleDateTime().toLocalDate(),
                recoveryRatCertificateDataDtoList.getMemberStateOfTest(),
                DateHelper.calculateValidFrom(recoveryRatCertificateDataDtoList.getSampleDateTime().toLocalDate()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryRatCertificateDataDtoList.getSampleDateTime().toLocalDate()),
                ISSUER,
                UVCI.generateUVCI(recoveryRatCertificateDataDtoList.toString())
        );
    }
}
