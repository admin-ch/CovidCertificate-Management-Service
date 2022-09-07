package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AntibodyCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AntibodyCertificatePdfMapper {

    public static AntibodyCertificatePdf toAntibodyCertificatePdf(
            AntibodyCertificateCreateDto antibodyCertificateCreateDto,
            AntibodyCertificateQrCode qrCodeData,
            String countryOfTest,
            String countryOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new AntibodyCertificatePdf(
                antibodyCertificateCreateDto.getPersonData().getName().getFamilyName(),
                antibodyCertificateCreateDto.getPersonData().getName().getGivenName(),
                antibodyCertificateCreateDto.getPersonData().getDateOfBirth(),
                antibodyCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                antibodyCertificateCreateDto.getAntibodyInfo().get(0).getSampleDate(),
                antibodyCertificateCreateDto.getAntibodyInfo().get(0).getTestingCenterOrFacility(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                qrCodeData.getAntibodyInfo().get(0).getIdentifier());
    }
}
