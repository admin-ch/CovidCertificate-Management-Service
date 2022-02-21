package ch.admin.bag.covidcertificate.api.valueset;

import java.util.Arrays;
import java.util.Optional;

public enum TestType {
    PCR("LP6464-4", "Nucleic acid amplification with probe detection (PCR)"),
    RAPID_TEST("LP217198-3", "Rapid immunoassay"),
    ANTIBODY_TEST("94504-8", "Antibody test"),
    EXCEPTIONAL_TEST("medical-exemption", "Medical exemption");

    public final String typeCode;
    public final String typeDisplay;

    TestType(String typeCode, String typeDisplay) {
        this.typeCode = typeCode;
        this.typeDisplay = typeDisplay;
    }

    public static Optional<TestType> findByTypeCode(String thatTypeCode) {
        return Arrays.stream(TestType.values())
                     .filter(testType -> testType.typeCode.equalsIgnoreCase(thatTypeCode))
                     .findFirst();
    }
}