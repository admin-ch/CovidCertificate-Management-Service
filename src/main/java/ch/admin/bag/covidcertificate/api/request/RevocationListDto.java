package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SIZE_OF_UVCI_LIST;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class RevocationListDto {
    private List<UvciForRevocationDto> uvcis;
    private SystemSource systemSource;
    private String userExtId;

    public void validateList() {
        if (uvcis.size() > 100) {
            throw new RevocationException(INVALID_SIZE_OF_UVCI_LIST);
        }
        for(UvciForRevocationDto uvci: uvcis) {
            uvci.validate();
        }
    }
}