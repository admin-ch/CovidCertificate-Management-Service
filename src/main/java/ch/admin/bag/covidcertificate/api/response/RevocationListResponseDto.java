package ch.admin.bag.covidcertificate.api.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RevocationListResponseDto {
    private Map<String, String> uvcisToErrorMessage;
    private List<String> revokedUvcis;
}
