package ch.admin.bag.covidcertificate.config.cleanup;

import lombok.Data;

@Data
public class Database {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
