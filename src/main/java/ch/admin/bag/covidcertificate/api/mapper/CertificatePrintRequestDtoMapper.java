package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificatePrintRequestDtoMapper {
    public static CertificatePrintRequestDto toCertificatePrintRequestDto(byte[] pdf, String uvci, CertificateCreateDto createDto) {
        CovidCertificateAddressDto addressDto = createDto.getAddress();
        String addressLine1 = createDto.getPersonData().getName().getGivenName() + " " + createDto.getPersonData().getName().getFamilyName();
        return new CertificatePrintRequestDto(pdf, uvci, addressLine1, addressDto.getStreetAndNr(),
                addressDto.getZipCode(), addressDto.getCity(), createDto.getLanguage());
    }
}
