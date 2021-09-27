package ch.admin.bag.covidcertificate.config;

import ch.admin.bag.covidcertificate.service.SwissDGCBarcodeEncoder;
import ch.admin.bag.covidcertificate.service.SwissDGCSigner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;

@Configuration
public class BarcodeConfig {

    @Bean
    public SwissDGCBarcodeEncoder getDGCBarcodeEncoder(SwissDGCSigner dgcSigner) {
        var defaultBarcodeCreator = new DefaultBarcodeCreator();
        return new SwissDGCBarcodeEncoder(dgcSigner, defaultBarcodeCreator);
    }
}
