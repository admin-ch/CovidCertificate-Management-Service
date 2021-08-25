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

        var rapidTests = new ArrayList<RapidTestDto>();
        rapidTests.add(new RapidTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)", true));
        rapidTests.add(new RapidTestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2", true));
        return rapidTests;
    }

    @PostMapping("/issuable-rapid-tests")
    public List<IssuableRapidTestDto> getIssuableRapidTests(HttpServletRequest request) {
        log.info("Call of getIssuableRapidTests for value sets");
        securityHelper.authorizeUser(request);

        var issuableRapidTests = new ArrayList<IssuableRapidTestDto>();
        issuableRapidTests.add(new IssuableRapidTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)"));
        issuableRapidTests.add(new IssuableRapidTestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2"));
        return issuableRapidTests;
    }

    @PostMapping("/vaccines")
    public List<VaccineDto> getVaccines(HttpServletRequest request) {
        log.info("Call of getVaccines for value sets");
        securityHelper.authorizeUser(request);

        var vaccines = new ArrayList<VaccineDto>();
        vaccines.add(new VaccineDto("EU/1/20/1528", "Comirnaty", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100030215", "Biontech Manufacturing GmbH", true));
        vaccines.add(new VaccineDto("EU/1/20/1507", "COVID-19 Vaccine Moderna", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100031184", "Moderna Biotech Spain S.L.", true));
        return vaccines;
    }

    @PostMapping("/issuable-vaccines")
    public List<IssuableVaccineDto> getIssuableVaccines(HttpServletRequest request) {
        log.info("Call of getIssuableVaccines for value sets");
        securityHelper.authorizeUser(request);

        var issuableVaccines = new ArrayList<IssuableVaccineDto>();
        issuableVaccines.add(new IssuableVaccineDto("EU/1/20/1528", "Comirnaty", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100030215", "Biontech Manufacturing GmbH"));
        issuableVaccines.add(new IssuableVaccineDto("EU/1/20/1507", "COVID-19 Vaccine Moderna", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100031184", "Moderna Biotech Spain S.L."));
        return issuableVaccines;
    }
}
