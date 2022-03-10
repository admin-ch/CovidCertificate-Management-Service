package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.authorization.config.RoleData;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Profile("authorization")
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    public static String SRVC_WEB = "web-ui";
    public static String SRVC_API = "api-gateway";
    public static String SRVC_MGMT = "management";
    public static String SRVC_REPORT = "report";

    private final AuthorizationConfig authorizationConfig;
    private final RoleConfig roleConfig;
    private Map<String, ServiceData> services;
    private Map<String, String> roleMapping;

    /**
     * Returns all permitted functions by given roles at given service.
     * This permission is bound to time and may change during time.
     *
     * @param service   the requesting service
     * @param rawRoles  the current roles of the user (either from eIAM or from Claim)
     * @return list of permitted functions
     */
    public Set<String> getCurrent(String service, List<String> rawRoles) {
        Set<String> grantedFunctions = Collections.emptySet();
        // check support for given service
        ServiceData serviceData = services.get(service);
        if (serviceData == null) {
            log.info("service '{}' unknown", service);
        } else {
            // map the raw roles to the configured roles
            final Set<String> roles = rawRoles.stream()
                    .map(role -> roleMapping.get(role))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (roles.isEmpty()) {
                log.info("no supported roles in '{}'", rawRoles);
            } else {
                // keep authorizations which are currently valid
                val functions = serviceData.getFunctions();
                List<ServiceData.Function> functionsByPointInTime =
                        filterByPointInTime(LocalDateTime.now(), functions.values());
                // identify the functions granted to this time by given roles
                grantedFunctions = functionsByPointInTime.stream()
                        .filter(function -> isGranted(roles, function))
                        .map(function -> function.getIdentifier())
                        .collect(Collectors.toSet());
            }
        }
        log.info("grants: "+grantedFunctions);
        return grantedFunctions;
    }

    /**
     * Returns the definition of given service.
     *
     * @param service the requesting service
     * @return the service's definition
     */
    public ServiceData getDefinition(String service) {
        return services.get(service);
    }

    public List<RoleData> getRoleMapping() {
        return roleConfig.getMappings();
    }

    /**
     * Returns <code>true</code> for given function IF:
     * <ul>
     *     <li>mandatory</li>
     *     is valid when either is <code>null</code> or the given role is part of the user's roles
     *     <li>one-of</li>
     *     is valid when either is <code>null</code> or one of the given roles is part of the user's roles
     * </ul>
     * <li>
     * The given function is only permitted when both conditions are valid.

     * @param roles  the user's roles
     * @param function the function to check
     * @return <code>true</code> only if both mandatory and one-of are valid
     */
    private boolean isGranted(Set<String> roles, ServiceData.Function function) {
        //lets
        boolean allAdditionalValid = true;
        if (function.getAdditional() != null) {
            // check additional functions which are currently valid
            List<ServiceData.Function> addFunctionsByPointInTime =
                    filterByPointInTime(LocalDateTime.now(), function.getAdditional());

            allAdditionalValid = addFunctionsByPointInTime.stream().allMatch(func -> isGranted(roles, func));
        }
        List<String> oneOf = function.getOneOf();
        boolean oneOfValid = true;
        oneOfValid = oneOf.stream().anyMatch(roles::contains);
        return (allAdditionalValid && oneOfValid);
    }

    private List<ServiceData.Function> filterByPointInTime(LocalDateTime pointInTime, Collection<ServiceData.Function> functions) {
        List<ServiceData.Function> result = Collections.emptyList();
        if (functions != null && pointInTime != null) {
            result = functions.stream()
                    .parallel()
                    .filter(function -> isBetween(pointInTime, function))
                    .collect(Collectors.toList());
        }
        return result;
    }

    private boolean isBetween(LocalDateTime pointInTime, ServiceData.Function function) {
        boolean between = false;
        if (function != null) {
            boolean fromSmallerEquals = (function.getFrom()==null || function.getFrom().isBefore(pointInTime) || function.getFrom().isEqual(pointInTime));
            boolean untilLargerEquals = (function.getUntil()==null || function.getUntil().isAfter(pointInTime) || function.getUntil().isEqual(pointInTime));
            between = fromSmallerEquals && untilLargerEquals;
        }
        return between;
    }

    @PostConstruct
    private void init() {
        services = new TreeMap<>();
        services.put("api-gateway", enrichServiceData(authorizationConfig.getApiGateway()));
        services.put("management", enrichServiceData(authorizationConfig.getManagement()));
        services.put("web-ui", enrichServiceData(authorizationConfig.getWebUi()));

        roleMapping = new TreeMap<>();
        for (RoleData roleData : roleConfig.getMappings()) {
            if (roleMapping.containsKey(roleData.getClaim()) || roleMapping.containsKey(roleData.getEiam())) {
                throw new IllegalStateException(MessageFormat.format(
                        "role mappings for \"{0}\" not unique (conflicts with either eiam \"{1}\" or claim \"{2}\")",
                        roleData.getIntern(), roleData.getEiam(), roleData.getClaim()));
            } else {
                roleMapping.put(roleData.getClaim(), roleData.getIntern());
                roleMapping.put(roleData.getEiam(), roleData.getIntern());
            }
        }
    }

    private ServiceData enrichServiceData(ServiceData serviceData){
        serviceData.getFunctions().values().stream()
                .forEach(function -> enrichFunction(function, serviceData.getFunctions()));
        return serviceData;
    }

    private ServiceData.Function enrichFunction(ServiceData.Function function, Map<String, ServiceData.Function> repo){
        function.setAdditional(buildAdditionalList(function.getAdditionalRef(),repo));
        if (function.getUri() == null) {
            function.setUri(Collections.<String>emptyList());
        }
        if (function.getOneOf() == null) {
            function.setOneOf(Collections.<String>emptyList());
        }
        return function;
    }

    private List<ServiceData.Function> buildAdditionalList(List<String> refs, Map<String, ServiceData.Function> repo){
        List<ServiceData.Function> result = new ArrayList<ServiceData.Function>();
        if (refs != null)
        for (String ref: refs) {
            ServiceData.Function func = repo.get(ref);
            if (func != null){
                //found matching function
                result.add(func);
            } else {
                throw new IllegalStateException(MessageFormat.format("referenced Function in Authorization Config not found: \"{0}\"", ref));
            }

        }
        return result;
    }
}
