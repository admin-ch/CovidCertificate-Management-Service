package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Profile("authorization")
@RestController
@RequestMapping("/api/v1/authorization")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @GetMapping("/current/{service}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public Set<String> getCurrent(@PathVariable String service, @RequestBody UserDto user) {
        log.info("current authorization service={} user={}", service, user);
        Set<String> result = authorizationService.getCurrent(service, user.getRoles());
        log.info("found: "+result);
        return result;
    }

    @GetMapping("/definition/{service}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ServiceData getDefinition(@PathVariable String service) {
        log.info("authorization service={}", service);
        ServiceData result = authorizationService.getDefinition(service);
        log.info("found: "+result);
        return result;
    }
}
