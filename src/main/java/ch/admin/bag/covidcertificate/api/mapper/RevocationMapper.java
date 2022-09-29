package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.domain.Revocation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RevocationMapper {
    public static Revocation toRevocation(String uvci, boolean fraud, LocalDateTime deletedDateTime) {
        return new Revocation(uvci, fraud, deletedDateTime);
    }
}
