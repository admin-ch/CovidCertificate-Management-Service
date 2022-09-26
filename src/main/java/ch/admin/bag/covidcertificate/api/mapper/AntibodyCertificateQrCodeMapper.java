package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AntibodyCertificateData;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AntibodyCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AntibodyCertificateQrCodeMapper {

    public static AntibodyCertificateQrCode toAntibodyCertificateQrCode(AntibodyCertificateCreateDto antibodyCertificateCreateDto) {
        return new AntibodyCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(antibodyCertificateCreateDto.getPersonData()),
                AntibodyCertificateQrCodeMapper.toAntibodyCertificateDataList(
                        antibodyCertificateCreateDto.getAntibodyInfo())
        );
    }

    private static List<AntibodyCertificateData> toAntibodyCertificateDataList(List<AntibodyCertificateDataDto> antibodyCertificateDataDtoList) {
        return antibodyCertificateDataDtoList.stream().map(
                AntibodyCertificateQrCodeMapper::toAntibodyCertificateData
        ).collect(Collectors.toList());
    }

    private static AntibodyCertificateData toAntibodyCertificateData(AntibodyCertificateDataDto antibodyCertificateDataDto) {
        return new AntibodyCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                TestType.ANTIBODY_TEST.typeCode,
                Constants.EMPTY_STRING,
                antibodyCertificateDataDto.getSampleDate().atStartOfDay(ZoneId.systemDefault()),
                TestResult.POSITIVE.code,
                antibodyCertificateDataDto.getTestingCenterOrFacility(),
                Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND,
                ISSUER,
                UVCI.generateUVCI(antibodyCertificateDataDto.toString())
        );
    }
}
