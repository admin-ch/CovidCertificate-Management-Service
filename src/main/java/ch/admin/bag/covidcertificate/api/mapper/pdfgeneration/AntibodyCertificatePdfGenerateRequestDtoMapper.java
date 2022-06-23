package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AntibodyCertificatePdfGenerateRequestDtoMapper {

    public static AntibodyCertificatePdf toAntibodyCertificatePdf(
            AntibodyCertificatePdfGenerateRequestDto antibodyCertificateCreateDto,
            String countryOfTest,
            String countryOfTestEn
    ) {
        ZonedDateTime utcZoned = antibodyCertificateCreateDto.getDecodedCert().getAntibodyInfo().get(0).getSampleDateTime();
        ZonedDateTime swissZoned = utcZoned.withZoneSameInstant(SWISS_TIMEZONE);
        LocalDate swissLocalDate = swissZoned.toLocalDate();

        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new AntibodyCertificatePdf(
                antibodyCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                antibodyCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                antibodyCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                antibodyCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                swissLocalDate,
                antibodyCertificateCreateDto.getDecodedCert().getAntibodyInfo().get(0).getTestingCentreOrFacility(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                antibodyCertificateCreateDto.getDecodedCert().getAntibodyInfo().get(0).getIdentifier());
    }
}
