package ch.admin.bag.covidcertificate.web.controller.test;

import ch.admin.bag.covidcertificate.api.mapper.SigningInformationMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import ch.admin.bag.covidcertificate.service.test.TestCovidCertificateGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * /internal routes are not allowed to be whitelisted!
 *
 * The TestController can be used to verify current and future signing_information configuration.
 */
@RestController
@RequestMapping("/internal/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final SigningClient signingClient;
    private final SigningInformationRepository signingInformationRepository;
    private final TestCovidCertificateGenerationService testCovidCertificateGenerationService;

    @GetMapping("/{validAt}")
    public Map<SigningInformation, SigningInformationStatus> testSigningInformationConfiguration(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt) {

        Map<SigningInformation, SigningInformationStatus> checkedSigningInformationToStatus = new HashMap<>();
        for (SigningCertificateCategory signingCertificateCategory : SigningCertificateCategory.values()) {
            var signingInformationList = signingInformationRepository.findSigningInformation(
                    signingCertificateCategory.value,
                    validAt
            );

            for (SigningInformation signingInformation : signingInformationList) {
                try {
                    var messageBytes = UUID.randomUUID().toString().getBytes();
                    var signatureBytes = signingClient.createSignature(
                            messageBytes,
                            SigningInformationMapper.fromEntity(signingInformation)
                    );

                    if (StringUtils.isNotBlank(signingInformation.getCertificateAlias())) {
                        var message = Base64.getEncoder().encodeToString(messageBytes);
                        var signature = Base64.getEncoder().encodeToString(signatureBytes);

                        var verifySignatureDto = new VerifySignatureRequestDto(
                                message,
                                signature,
                                signingInformation.getCertificateAlias(),
                                signingInformation.getSlotNumber()
                        );
                        var validSignature = signingClient.verifySignature(verifySignatureDto);

                        if (validSignature) {
                            checkedSigningInformationToStatus.put(signingInformation, SigningInformationStatus.OK);
                        } else {
                            checkedSigningInformationToStatus.put(signingInformation, SigningInformationStatus.ERROR_INVALID);
                        }
                    } else {
                        checkedSigningInformationToStatus.put(signingInformation, SigningInformationStatus.WARN_NOT_PRESENT);
                    }
                } catch (Exception e) {
                    log.error("Error when testing certificate alias {} from slot {}", signingInformation.getCertificateAlias(), signingInformation.getSlotNumber(),  e);
                    checkedSigningInformationToStatus.put(signingInformation, SigningInformationStatus.ERROR);
                }
            }
        }
        return checkedSigningInformationToStatus;
    }

    @PostMapping("/vaccination/{validAt}")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/vaccination-tourist/{validAt}")
    public CovidCertificateCreateResponseDto createVaccinationTouristCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationTouristCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/test/{validAt}")
    public CovidCertificateCreateResponseDto createTestCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody TestCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/recovery/{validAt}")
    public CovidCertificateCreateResponseDto createRecoveryCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody RecoveryCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/recovery-rat/{validAt}")
    public CovidCertificateCreateResponseDto createRecoveryRatCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody RecoveryRatCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/antibody/{validAt}")
    public CovidCertificateCreateResponseDto createAntibodyCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody AntibodyCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/exceptional/{validAt}")
    public CovidCertificateCreateResponseDto createExceptionalCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody ExceptionalCertificateCreateDto createDto) throws IOException {

        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/conversion/vaccination/{validAt}")
    public ConvertedCertificateResponseDto convertVaccinationCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationCertificateConversionRequestDto conversionRequestDto) throws IOException {

        conversionRequestDto.validate();
        ConvertedCertificateResponseEnvelope convertedCertificateResponseEnvelope =
                testCovidCertificateGenerationService.convertFromExistingCovidCertificate(
                        conversionRequestDto,
                        validAt
                );
        log.info("Used key-id for conversion/vaccination: {}", convertedCertificateResponseEnvelope.getUsedKeyIdentifier());

        return convertedCertificateResponseEnvelope.getResponseDto();
    }
}
