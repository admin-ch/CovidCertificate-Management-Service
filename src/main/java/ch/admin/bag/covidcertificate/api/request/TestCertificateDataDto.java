package ch.admin.bag.covidcertificate.api.request;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCertificateDataDto extends BaseTestCertificateDataDto {

    private String manufacturerCode;

    private String typeCode;

    public TestCertificateDataDto(String manufacturerCode, String typeCode, ZonedDateTime sampleDateTime, String testingCentreOrFacility, String memberStateOfTest) {
        super(sampleDateTime, testingCentreOrFacility, memberStateOfTest);
        this.manufacturerCode = manufacturerCode;
        this.typeCode = typeCode;
    }
}
