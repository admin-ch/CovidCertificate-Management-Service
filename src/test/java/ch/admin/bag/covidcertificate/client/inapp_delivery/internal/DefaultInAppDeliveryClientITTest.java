package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static ch.admin.bag.covidcertificate.FixtureCustomization.createUVCI;
import static ch.admin.bag.covidcertificate.api.Constants.APP_DELIVERY_FAILED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@ActiveProfiles("local")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Disabled("Only runs manually")
class DefaultInAppDeliveryClientITTest {

    private static final String validTestCode = "BITBITBIT";

    @Autowired
    private DefaultInAppDeliveryClient client;

    @Test
    void deliverToApp_invalid() {
        var requestDto = new InAppDeliveryRequestDto("test", "test", "test");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                            () -> client.deliverToApp(createUVCI(), requestDto));

        assertEquals(APP_DELIVERY_FAILED,exception.getError());
    }

    @Test
    void deliverToApp_valid() {
        var requestDto = new InAppDeliveryRequestDto(validTestCode, "test", "test");
        assertDoesNotThrow(() -> client.deliverToApp(createUVCI(), requestDto));
    }
}
