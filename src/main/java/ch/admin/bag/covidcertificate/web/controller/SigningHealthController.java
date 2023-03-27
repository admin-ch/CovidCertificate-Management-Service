package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to check signing service health using the standard actuator and ping endpoints.
 */
@RestController
@RequestMapping("/api/v1/signing")
@RequiredArgsConstructor
@Slf4j
public class SigningHealthController {

    private final SigningClient signingClient;

    @GetMapping("/ping")
    public String checkPingOfSigningService() {
        return this.signingClient.callPing();
    }

    @GetMapping("/health")
    public String checkHealthOfSigningService() {
        return this.signingClient.callHealth();
    }

    @GetMapping("/info")
    public String checkInfoOfSigningService() {
        return this.signingClient.callInfo();
    }
}
