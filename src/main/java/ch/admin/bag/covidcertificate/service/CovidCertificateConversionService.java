package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.ConvertCertificateException;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificateQrCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateConversionService {
    private final BarcodeService barcodeService;
    private final ObjectMapper objectMapper;
    private final CovidCertificateDtoMapperService ccDtoMapperService;
    private final SigningInformationService signingInformationService;
    private final COSETime coseTime;
    private final RevocationService revocationService;

    public ConvertedCertificateResponseEnvelope convertFromExistingCovidCertificate(
            VaccinationCertificateConversionRequestDto conversionDto) throws JsonProcessingException {

        // check if uvci of origin certificate got revoked
        final String originUvci = conversionDto.getDecodedCert().getVaccinationInfo().get(0).getIdentifier();
        if (revocationService.isAlreadyRevoked(originUvci)) {
            throw new ConvertCertificateException(Constants.CONVERSION_UVCI_ALREADY_REVOKED, originUvci);
        }

        // map certificate data
        var qrCodeData = ccDtoMapperService.toVaccinationCertificateQrCodeForConversion(conversionDto);
        // take right signing information
        var signingInformation = signingInformationService.getVaccinationConversionSigningInformation();
        // define expiration of converted certificate
        var expiration24Months = coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS);
        // get the mapped UVCI, see toVaccinationCertificateQrCode
        var uvci = qrCodeData.getVaccinationInfo().get(0).getIdentifier();

        return generateCovidCertificate(qrCodeData, uvci, signingInformation, expiration24Months);
    }

    private ConvertedCertificateResponseEnvelope generateCovidCertificate(
            AbstractCertificateQrCode qrCodeData,
            String uvci,
            SigningInformationDto signingInformation,
            Instant expiration) throws JsonProcessingException {

        var contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.trace("Create barcode for conversion");
        var code = barcodeService.createBarcode(contents, signingInformation, expiration);
        var responseDto = new ConvertedCertificateResponseDto(code.getPayload(), uvci);
        responseDto.validate();
        var envelope = new ConvertedCertificateResponseEnvelope(
                responseDto,
                signingInformation.getCalculatedKeyIdentifier());
        return envelope;
    }
}
