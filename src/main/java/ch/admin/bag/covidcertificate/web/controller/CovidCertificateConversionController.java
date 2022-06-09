package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.service.CovidCertificateConversionService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/covidcertificate/conversion")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateConversionController {

    private static final String CONVERT_LOG = "Certificate converted with: {}";

    private final CovidCertificateConversionService covidCertificateConversionService;
    private final KpiDataService kpiLogService;

    @PostMapping("/vaccination")
    public ConvertedCertificateResponseDto convertVaccinationCertificate(
            @Valid @RequestBody VaccinationCertificateConversionRequestDto conversionRequestDto)
            throws IOException {

        log.info("Call of conversion for existing vaccination certificate");
        conversionRequestDto.validate();
        ConvertedCertificateResponseEnvelope responseEnvelope = covidCertificateConversionService
                .convertFromExistingCovidCertificate(conversionRequestDto);
        ConvertedCertificateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CONVERT_LOG, responseDto.getUvci());
        kpiLogService.logCertificateConversionKpi(
                conversionRequestDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }
}
