package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuableVaccineMapper {

    public static IssuableVaccineDto fromVaccine(Vaccine vaccine) {
        IssuableVaccineDto issuableVaccineDto = new IssuableVaccineDto(
                vaccine.getCode(),
                vaccine.getDisplay(),
                vaccine.getIssuable(),
                vaccine.isWhoEul()
        );
        Prophylaxis prophylaxis = vaccine.getProphylaxis();
        if (prophylaxis != null) {
            issuableVaccineDto.addProphylaxisInfo(prophylaxis);
        }
        AuthHolder authHolder = vaccine.getAuthHolder();
        if (authHolder != null) {
            issuableVaccineDto.addAuthHolderInfo(authHolder);
        }
        return issuableVaccineDto;
    }

    public static List<IssuableVaccineDto> fromVaccines(List<Vaccine> issuableVaccines) {
        if(issuableVaccines == null) {
            return Collections.emptyList();
        }
        return issuableVaccines.stream().map(IssuableVaccineMapper::fromVaccine).collect(Collectors.toList());
    }
}
