package ch.admin.bag.covidcertificate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;
import se.digg.dgc.service.DGCBarcodeEncoder;
import se.digg.dgc.service.impl.DefaultDGCBarcodeEncoder;
import se.digg.dgc.signatures.DGCSigner;

@Configuration
public class BarcodeConfig {

    @Bean
    public DGCBarcodeEncoder getDGCBarcodeEncoder(DGCSigner dgcSigner) {
        var defaultBarcodeCreator = new DefaultBarcodeCreator();
        return new DefaultDGCBarcodeEncoder(dgcSigner, defaultBarcodeCreator);
    }
}
