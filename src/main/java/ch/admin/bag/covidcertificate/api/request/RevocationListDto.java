package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI_IN_REQUEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SIZE_OF_UVCI_LIST;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class RevocationListDto {
    public static final int MIN_SIZE_LIST = 1;
    public static final int MAX_SIZE_LIST = 100;

    private List<UvciForRevocationDto> uvcis;
    private SystemSource systemSource;
    private String userExtId;

    public RevocationListDto(List<UvciForRevocationDto> uvcis, SystemSource systemSource) {
        this.uvcis = uvcis;
        this.systemSource = systemSource;
    }

    public void validate() {
        if (uvcis == null || uvcis.size() < MIN_SIZE_LIST || uvcis.size() > MAX_SIZE_LIST) {
            throw new RevocationException(INVALID_SIZE_OF_UVCI_LIST);
        }

        if (uvcis.stream().map(UvciForRevocationDto::getUvci).collect(Collectors.toSet()).size() < uvcis.size()) {
            throw new RevocationException(DUPLICATE_UVCI_IN_REQUEST);
        }
    }
}