package ch.admin.bag.covidcertificate.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileRegistry {

    public static final String SIGNING_SERVICE_MOCK = "mock-signing-service";
    public static final String PRINTING_QUEUE_MOCK = "mock-printing-queue";
}
