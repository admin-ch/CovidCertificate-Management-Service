package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RapidTestMapper {

    public static TestDto fromRapidTest(RapidTest rapidTest) {
        return new TestDto(rapidTest.getCode(),
                           rapidTest.getDisplay(),
                           null,
                           rapidTest.isActive());
    }

    public static List<TestDto> fromRapidTests(List<RapidTest> rapidTests) {
        if(rapidTests == null) {
            return Collections.emptyList();
        }
        return rapidTests.stream().map(RapidTestMapper::fromRapidTest).collect(Collectors.toList());
    }
}
