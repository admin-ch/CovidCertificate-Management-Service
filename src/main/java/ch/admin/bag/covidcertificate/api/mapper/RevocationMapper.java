package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.domain.Revocation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RevocationMapper {
    public static Revocation toRevocation(String uvci, boolean fraud) {
        return new Revocation(uvci, fraud);
    }
}
