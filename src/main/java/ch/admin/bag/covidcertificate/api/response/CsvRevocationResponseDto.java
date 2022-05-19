package ch.admin.bag.covidcertificate.api.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CsvRevocationResponseDto {
    private Integer uvcisWithErrorMessageCount;
    private Integer revokedUvcisCount;
    @NonNull
    private byte[] csv;
}
