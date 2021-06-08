package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/valuesets")
@RequiredArgsConstructor
@Slf4j
public class ValueSetsController {
    private final SecurityHelper securityHelper;
    private final ValueSetsService valueSetsService;

    @GetMapping()
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ValueSetsDto get(HttpServletRequest request) {
        log.info("Call to get value sets.");
        securityHelper.authorizeUser(request);

        return valueSetsService.getValueSets();
    }
}
