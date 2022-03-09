package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.domain.Revocation;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@ActiveProfiles({"local", "authorization"})
public class AuthorizationServiceTest {
    @Autowired
    private AuthorizationService authorizationService;

    @Test
    public void testGetDefinitions(){
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SRVC_WEB));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SRVC_API));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SRVC_MGMT));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SRVC_REPORT));
    }

    @Test
    public void testGetMappings(){
        assertNotNull(authorizationService.getRoleMapping());
    }

    @Test
    public void testSimpleGrant() {
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("bag-cc-web_ui_user");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificates-web")));
    }

    @Test
    public void testDelegatedGrant(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("bag-cc-web_ui_user");
        rawRoles.add("bag-cc-vacccert_creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-certificate")));
    }

    @Test
    public void testSimpleGrantEiam() {
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("9500.GGG-Covidcertificate.Web-UI-User");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificates-web")));
    }

    @Test
    public void testDelegatedGrantEiam(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("9500.GGG-Covidcertificate.Web-UI-User");
        rawRoles.add("9500.GGG-Covidcertificate.VaccCert-Creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }

    @Test
    public void testSimpleNoGrant(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("bag-cc-api_gw_user");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        //web base creator right must not be granted because right not assigned to API-GW user
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificates-web")));
    }

    @Test
    public void testDelegatedNoGrant(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("bag-cc-api_gw_user");
        rawRoles.add("bag-cc-vacccert_tourist_creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        //web creator right must not be granted because additional right ${service.webui.functions.create-certificate-web} not assigned to API-GW user
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }

    @Test
    public void testSimpleNoGrantEiam(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("9500.GGG-Covidcertificate.API-GW-User");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        //web base creator right must not be granted because right not assigned to API-GW user
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificates-web")));
    }

    @Test
    public void testDelegatedNoGrantEiam(){
        List<String> rawRoles = new ArrayList<String>();
        rawRoles.add("9500.GGG-Covidcertificate.API-GW-User");
        rawRoles.add("9500.GGG-Covidcertificate.VaccCert-Tourist-Creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SRVC_WEB, rawRoles);
        //web creator right must not be granted because additional right ${service.webui.functions.create-certificate-web} not assigned to API-GW user
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }

    //TODO right in future should not be applied today
}
