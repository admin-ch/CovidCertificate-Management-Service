package ch.admin.bag.covidcertificate.config;

import ch.admin.bag.covidcertificate.service.SwissDGCSigner;
import ch.admin.bag.covidcertificate.service.COSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.digg.dgc.signatures.DGCSigner;

@Configuration
@RequiredArgsConstructor
public class COSEConfig {

    private final COSEService coseService;

    @Bean
    public DGCSigner getDGCSigner() {
        return SwissDGCSigner.builder()
                .coseService(coseService)
                .build();
    }
}
