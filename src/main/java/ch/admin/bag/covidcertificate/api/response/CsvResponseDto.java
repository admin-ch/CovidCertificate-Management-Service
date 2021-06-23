package ch.admin.bag.covidcertificate.api.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class CsvResponseDto {
    @NonNull
    private byte[] zip;
}
