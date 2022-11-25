package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@Profile(ProfileRegistry.VACCINE_SETS_SERVICE_MOCK)
public class MockVaccineValueSetsClient implements VaccineValueSetsClient {

    public static final String HTTPS_NOT_VALID_HOST = "https://not_valid_host";
    public static final String DISPLAY_VACCINE = "vaccine";
    public static final String DISPLAY_AUTH_HOLDER = "authHolder";
    public static final String DISPLAY_PROPHYLAXIS = "prophylaxis";

    @Override
    public Map<String, VaccineValueSetDto> getVaccineValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getVaccineValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new VaccineValueSetDto(DISPLAY_VACCINE, "", true, HTTPS_NOT_VALID_HOST, ""));
    }

    @Override
    public Map<String, AuthHolderValueSetDto> getAuthHolderValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getAuthHolderValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new AuthHolderValueSetDto(DISPLAY_AUTH_HOLDER, "", true, HTTPS_NOT_VALID_HOST, ""));
    }

    @Override
    public Map<String, ProphylaxisValueSetDto> getProphylaxisValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getProphylaxisValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new ProphylaxisValueSetDto(DISPLAY_PROPHYLAXIS, "", true, HTTPS_NOT_VALID_HOST, ""));
    }
}
