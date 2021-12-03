package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.exception.RevocationError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CheckRevocationListResponseDto {
    private RevocationError revocationError;
    private List<String> revocableUvcis;
    private String failingUvci;
}
