package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccineMapper {

    public static VaccineDto fromVaccine(Vaccine vaccine) {
        return new VaccineDto(
                vaccine.getCode(),
                vaccine.getDisplay(),
                vaccine.getProphylaxis().getCode(),
                vaccine.getProphylaxis().getDisplay(),
                vaccine.getAuthHolder().getCode(),
                vaccine.getAuthHolder().getDisplay(),
                (vaccine.isActive() && vaccine.getAuthHolder().isActive() && vaccine.getProphylaxis().isActive()));
    }

    public static List<VaccineDto> fromVaccines(List<Vaccine> vaccines) {
        if (vaccines == null) {
            return null;
        }
        return vaccines.stream().map(VaccineMapper::fromVaccine).collect(Collectors.toList());
    }
}
