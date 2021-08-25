package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.mapper.ValueSetsResponseDtoMapper;
import ch.admin.bag.covidcertificate.api.response.*;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuesets")
@RequiredArgsConstructor
@Slf4j
public class ValueSetsController {
    private final SecurityHelper securityHelper;
    private final ValueSetsService valueSetsService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ValueSetsResponseDto get(HttpServletRequest request) {
        log.info("Call to get value sets.");
        securityHelper.authorizeUser(request);

        return ValueSetsResponseDtoMapper.create(valueSetsService.getValueSets());
    }

    @PostMapping("/rapid-tests")
    public List<RapidTestDto> getRapidTests(HttpServletRequest request) {
        log.info("Call of getRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getRapidTests();
    }

    @PostMapping("/issuable-rapid-tests")
    public List<IssuableRapidTestDto> getIssuableRapidTests(HttpServletRequest request) {
        log.info("Call of getIssuableRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getIssuableRapidTests();
    }

    @PostMapping("/vaccines")
    public List<VaccineDto> getVaccines(HttpServletRequest request) {
        log.info("Call of getVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getVaccines();
    }

    @PostMapping("/issuable-vaccines")
    public List<IssuableVaccineDto> getIssuableVaccines(HttpServletRequest request) {
        log.info("Call of getIssuableVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getIssuableVaccines();
    }
}
