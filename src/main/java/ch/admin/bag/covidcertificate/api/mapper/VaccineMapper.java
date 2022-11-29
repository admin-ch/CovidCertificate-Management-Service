package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
                (vaccine.isActive() && vaccine.getAuthHolder().isActive() && vaccine.getProphylaxis().isActive()),
                vaccine.getIssuable(),
                vaccine.isWhoEul()
        );
    }

    public static List<VaccineDto> fromVaccines(List<Vaccine> vaccines) {
        if (vaccines == null) {
            return Collections.emptyList();
        }
        return vaccines.stream().map(VaccineMapper::fromVaccine).collect(Collectors.toList());
    }

    public static List<VaccineDto> uniqueVaccines(List<Vaccine> vaccines) {
        if (vaccines == null) {
            return Collections.emptyList();
        }
        Map<String, VaccineDto> result = new HashMap<>();
        for (Vaccine vaccine : vaccines) {
            if (!result.containsKey(vaccine.getCode())) {
                VaccineDto resultDto = VaccineMapper.fromVaccine(vaccine);
                resultDto.setIssuable(Issuable.UNDEFINED);
                result.put(vaccine.getCode(), resultDto);
            }
        }
        return new LinkedList<>(result.values());
    }
}
