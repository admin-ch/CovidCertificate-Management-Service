package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.domain.Revocation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RevocationMapper {
    public static Revocation toRevocation(RevocationDto revocationDto) {
        return new Revocation(revocationDto.getUvci());
    }
}
