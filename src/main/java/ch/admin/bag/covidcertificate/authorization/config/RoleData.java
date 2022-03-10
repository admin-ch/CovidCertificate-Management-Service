package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleData {

    private String intern;
    private String eiam;
    private String claim;
}
