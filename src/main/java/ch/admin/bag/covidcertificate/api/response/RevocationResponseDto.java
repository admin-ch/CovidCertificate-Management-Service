package ch.admin.bag.covidcertificate.api.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RevocationResponseDto {
    private RevocationStatus status;
    private LocalDateTime revocationDateTime;

    public RevocationResponseDto(RevocationStatus status) {
        this.status = status;
        revocationDateTime = null;
    }
}
