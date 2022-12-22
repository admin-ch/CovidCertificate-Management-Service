package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.RoleData;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static net.logstash.logback.argument.StructuredArguments.kv;


@RestController
@RequestMapping("/api/v1/authorization")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController {

    public static final String FOUND = "found: ";

    private final AuthorizationService authorizationService;

    @GetMapping("/current/{service}")
    public Set<String> getCurrent(@PathVariable String service, @RequestParam List<String> roles) {
        log.info("Get current authorization: {} {}", kv("service", service), kv("roles", roles));
        Set<String> result = authorizationService.getCurrent(service, roles);
        log.info(FOUND + result);
        return result;
    }

    @GetMapping("/definition/{service}")
    public ServiceData getDefinition(@PathVariable String service) {
        log.info("authorization service={}", service);
        ServiceData result = authorizationService.getDefinition(service);
        log.info(FOUND + result);
        return result;
    }

    @GetMapping("/role-mapping")
    public List<RoleData> getRoleMapping() {
        log.info("authorization role-mapping");
        List<RoleData> result = authorizationService.getRoleMapping();
        log.info(FOUND + result);
        return result;
    }
}
