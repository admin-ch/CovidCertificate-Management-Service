package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.mapper.ValueSetsResponseDtoMapper;
import ch.admin.bag.covidcertificate.api.response.ValueSetsResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuesets")
@RequiredArgsConstructor
@Slf4j
public class ValueSetsController {
    private final SecurityHelper securityHelper;
    private final ValueSetsService valueSetsService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ValueSetsResponseDto get(HttpServletRequest request) {
        log.info("Call to get value sets.");
        securityHelper.authorizeUser(request);

        return ValueSetsResponseDtoMapper.create(valueSetsService.getValueSets());
    }

    @GetMapping("/rapid-tests")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<TestDto> getRapidTests(HttpServletRequest request) {
        log.info("Call of getRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getRapidTests();
    }

    @GetMapping("/issuable-rapid-tests")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableTestDto> getIssuableRapidTests(HttpServletRequest request) {
        log.info("Call of getIssuableRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getIssuableRapidTests();
    }

    @GetMapping("/vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<VaccineDto> getVaccines(HttpServletRequest request) {
        log.info("Call of getVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getVaccines();
    }

    @GetMapping("/issuable-vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableVaccineDto> getApiIssuableVaccines(HttpServletRequest request) {
        log.info("Call of getApiIssuableVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getApiGatewayIssuableVaccines();
    }

    @GetMapping("/api-platform-issuable-vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableVaccineDto> getApiPlatformIssuableVaccines(HttpServletRequest request) {
        log.info("Call of getApiPlatformIssuableVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getApiPlatformIssuableVaccines();
    }

    @GetMapping("/web-ui-issuable-vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableVaccineDto> getWebUiIssuableVaccines(HttpServletRequest request) {
        log.info("Call of getWebUiIssuableVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getWebUiIssuableVaccines();
    }
}
