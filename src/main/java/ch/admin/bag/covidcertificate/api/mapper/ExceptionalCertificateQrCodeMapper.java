package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.qrcode.ExceptionalCertificateData;
import ch.admin.bag.covidcertificate.service.domain.qrcode.ExceptionalCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionalCertificateQrCodeMapper {

    public static ExceptionalCertificateQrCode toExceptionalCertificateQrCode(ExceptionalCertificateCreateDto exceptionalCertificateCreateDto) {
        return new ExceptionalCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(exceptionalCertificateCreateDto.getPersonData()),
                ExceptionalCertificateQrCodeMapper.toExceptionalCertificateDataList(
                        exceptionalCertificateCreateDto.getExceptionalInfo())
        );
    }

    private static List<ExceptionalCertificateData> toExceptionalCertificateDataList(List<ExceptionalCertificateDataDto> exceptionalCertificateDataDtoList) {
        return exceptionalCertificateDataDtoList.stream().map(
                ExceptionalCertificateQrCodeMapper::toExceptionalCertificateData
        ).toList();
    }

    private static ExceptionalCertificateData toExceptionalCertificateData(ExceptionalCertificateDataDto exceptionalCertificateDataDto) {
        return new ExceptionalCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                TestType.EXCEPTIONAL_TEST.typeCode,
                exceptionalCertificateDataDto.getValidFrom().atStartOfDay(ZoneId.systemDefault()),
                TestResult.POSITIVE.code,
                exceptionalCertificateDataDto.getAttestationIssuer(),
                Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND,
                ISSUER,
                UVCI.generateUVCI(exceptionalCertificateDataDto.toString())
        );
    }
}
