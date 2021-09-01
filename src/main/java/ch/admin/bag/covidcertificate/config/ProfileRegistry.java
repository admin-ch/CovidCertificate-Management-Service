package ch.admin.bag.covidcertificate.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileRegistry {
    public static final String SIGNING_SERVICE_MOCK = "mock-signing-service";
    public static final String PRINTING_SERVICE_MOCK = "mock-printing-service";
    public static final String INAPP_DELIVERY_SERVICE_MOCK = "mock-inapp-delivery-service";
    public static final String VALUE_SETS_SERVICE_MOCK = "mock-value-sets-service";
}
