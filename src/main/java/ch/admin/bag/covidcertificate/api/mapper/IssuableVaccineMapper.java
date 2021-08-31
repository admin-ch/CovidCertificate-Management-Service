package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuableVaccineMapper {

    public static IssuableVaccineDto fromVaccine(Vaccine vaccine) {
        return new IssuableVaccineDto(vaccine.getCode(), vaccine.getDisplay(), vaccine.getProphylaxisCode(),
                              vaccine.getProphylaxisDisplayName(), vaccine.getAuthHolderCode(),
                              vaccine.getAuthHolderDisplayName());
    }

    public static List<IssuableVaccineDto> fromVaccines(List<Vaccine> issuableVaccines) {
        if(issuableVaccines == null) {
            return null;
        }
        return issuableVaccines.stream().map(IssuableVaccineMapper::fromVaccine).collect(Collectors.toList());
    }
}
