package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IssuableRapidTestMapper {

    public static IssuableTestDto fromRapidTest(RapidTest rapidTest) {
        return new IssuableTestDto(rapidTest.getCode(), rapidTest.getDisplay(), TestType.RAPID_TEST, rapidTest.getValidUntil());
    }

    public static List<IssuableTestDto> fromRapidTests(List<RapidTest> rapidTests) {
        if(rapidTests == null) {
            return Collections.emptyList();
        }
        return rapidTests.stream().map(IssuableRapidTestMapper::fromRapidTest).toList();
    }
}
