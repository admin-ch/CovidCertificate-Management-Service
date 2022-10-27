package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
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

    public Map<String, ValueSetDto> getValueSets(String valueSetId) {
        log.info("Call the mock getValueSets service");
        return Map.of("1", new ValueSetDto("test", true, null));
    }

    @Override
    public Map<String, VaccineValueSetDto> getVaccineValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getVaccineValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new VaccineValueSetDto("vaccine", "", true, "https://not_valid_host", ""));
    }

    @Override
    public Map<String, AuthHolderValueSetDto> getAuthHolderValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getAuthHolderValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new AuthHolderValueSetDto("authHolder", "", true, "https://not_valid_host", ""));
    }

    @Override
    public Map<String, ProphylaxisValueSetDto> getProphylaxisValueSet(VaccineImportControl vaccineImportControl) {
        log.info("Call the mock getProphylaxisValueSet service for version {}", vaccineImportControl.getImportVersion());
        return Map.of("1", new ProphylaxisValueSetDto("prophylaxis", "", true, "https://not_valid_host", ""));
    }
}
