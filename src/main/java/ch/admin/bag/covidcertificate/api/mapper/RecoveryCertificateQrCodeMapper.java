package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
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
public class RecoveryCertificateQrCodeMapper {

    public static RecoveryCertificateQrCode toRecoveryCertificateQrCode(RecoveryCertificateCreateDto recoveryCertificateCreateDto) {
        return new RecoveryCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(recoveryCertificateCreateDto.getPersonData()),
                RecoveryCertificateQrCodeMapper.toRecoveryCertificateDataList(
                        recoveryCertificateCreateDto.getRecoveryInfo())
        );
    }

    private static List<RecoveryCertificateData> toRecoveryCertificateDataList(List<RecoveryCertificateDataDto> recoveryCertificateDataDtoList) {
        return recoveryCertificateDataDtoList.stream().map(
                RecoveryCertificateQrCodeMapper::toRecoveryCertificateData
        ).collect(Collectors.toList());
    }

    private static RecoveryCertificateData toRecoveryCertificateData(RecoveryCertificateDataDto recoveryCertificateDataDto) {
        return new RecoveryCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                recoveryCertificateDataDto.getDateOfFirstPositiveTestResult(),
                recoveryCertificateDataDto.getCountryOfTest(),
                DateHelper.calculateValidFrom(recoveryCertificateDataDto.getDateOfFirstPositiveTestResult()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryCertificateDataDto.getDateOfFirstPositiveTestResult()),
                ISSUER,
                UVCI.generateUVCI(recoveryCertificateDataDto.toString())
        );
    }
}
