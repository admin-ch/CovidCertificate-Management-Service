package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuableRapidTestMapper {

    public static IssuableTestDto fromRapidTest(RapidTest rapidTest) {
        return new IssuableTestDto(rapidTest.getCode(), rapidTest.getDisplay(), null);
    }

    public static List<IssuableTestDto> fromRapidTests(List<RapidTest> rapidTests) {
        if(rapidTests == null) {
            return null;
        }
        return rapidTests.stream().map(IssuableRapidTestMapper::fromRapidTest).collect(Collectors.toList());
    }
}
