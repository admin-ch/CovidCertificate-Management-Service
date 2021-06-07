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
        return new CertificatePrintRequestDto(pdf, uvci, addressDto.getLine1(), addressDto.getLine2(), "",
                addressDto.getZipCode(), addressDto.getCity(), createDto.getLanguage());
    }
}
