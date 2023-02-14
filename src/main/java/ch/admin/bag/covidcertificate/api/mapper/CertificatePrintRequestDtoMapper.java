package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class CertificatePrintRequestDtoMapper {

    @Value("${cc-printing-service.billing.since-vaccination-date}")
    private @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sinceVaccinationDate;

    public CertificatePrintRequestDto toCertificatePrintRequestDto(byte[] pdf, String uvci, CertificateCreateDto createDto) {
        CovidCertificateAddressDto addressDto = createDto.getAddress();
        String addressLine1 = createDto.getPersonData().getName().getGivenName() + " " + createDto.getPersonData().getName().getFamilyName();
        return new CertificatePrintRequestDto(pdf, uvci, addressLine1, addressDto.getStreetAndNr(),
                addressDto.getZipCode(), addressDto.getCity(), createDto.getLanguage(), createDto.getAddress().getCantonCodeSender(), isBillable(createDto));
    }

    private boolean isBillable(CertificateCreateDto createDto){
        var vaccinationDate = createDto instanceof VaccinationCertificateCreateDto creationDto ? creationDto.getCertificateData().get(0).getVaccinationDate() : null;
        return vaccinationDate != null &&
                vaccinationDate.compareTo(sinceVaccinationDate) >= 0;
    }
}
