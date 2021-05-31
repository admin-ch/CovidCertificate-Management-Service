package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.service.RevocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/revocation-list")
@RequiredArgsConstructor
@Slf4j
public class RevocationListController {
    private final RevocationService revocationService;

    @GetMapping()
    public List<String> get() {
        log.info("Call of get revocations.");
        return revocationService.getRevocations();
    }

}
