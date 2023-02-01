package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.parsing.StringNotEmptyToUppercaseElseNullDeserializer;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages;
import ch.admin.bag.covidcertificate.util.DateHelper;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.validation.*;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CertificateCreateDto {

    @NotNull(message = "No vaccination data was specified")
    @Size(min = 1, message = "No vaccination data was specified")
    private List<@Valid VaccinationCertificateDataDto> certificateData;

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isBirthdateAfter() {
        if (Objects.isNull(getPersonData().getDateOfBirth()) || Objects.isNull(certificateData)) return false;
        try {
            return certificateData.stream().anyMatch(dto -> isBirthdateAfter(dto.getVaccinationDate()));
        } catch (CreateCertificateException e) {
            return true;
        }
    }

    @JsonUnwrapped
    @NotNull(message = "personData must not be null")
    @Valid
    private CovidCertificatePersonDto personData;

    private String language;

    @Valid
    private CovidCertificateAddressDto address;

    @JsonDeserialize(using = StringNotEmptyToUppercaseElseNullDeserializer.class)
    private String appCode;

    @NotNull(message = "systemSource must not be null")
    private SystemSource systemSource;
    private String userExtId;

    @AssertTrue(message = "The given language does not match any of the supported languages: de, it, fr, rm!")
    public boolean isLanguageValid() {
        return AcceptedLanguages.isAcceptedLanguage(language);
    }

    @AssertFalse(message = "Delivery method can either be InApp or print, but not both.")
    public boolean isDuplicateDeliveryMethod() {
        return this.sendToPrint() && StringUtils.hasText(this.appCode);
    }

    @AssertTrue(message = "Print is not available for test certificates")
    public boolean isValidPrint() {
        if (!this.sendToPrint() || !StringUtils.hasText(this.appCode)) {
            if (this.sendToPrint()) {
                return this.isDeliverablePerPost();
            }
        }
        return true;
    }

    @AssertTrue(message = "App code is in an invalid format.")
    public boolean isValidAppCode() {
        if (this.sendToPrint()) {
            return true;
        }
        return this.sendToApp() && org.apache.commons.lang3.StringUtils.isAlphanumeric(this.appCode);
    }

    protected CertificateCreateDto(CovidCertificatePersonDto personData, String language, CovidCertificateAddressDto address, String appCode, SystemSource systemSource) {
        this.personData = personData;
        this.language = language;
        this.address = address;
        this.appCode = StringUtils.hasText(appCode) ? StringUtils.trimAllWhitespace(appCode).toUpperCase() : null;
        this.systemSource = systemSource;
    }

    protected CertificateCreateDto(CovidCertificatePersonDto personData, String language, CovidCertificateAddressDto address, String appCode, SystemSource systemSource, List<VaccinationCertificateDataDto> certificateData) {
        this.personData = personData;
        this.language = language;
        this.address = address;
        this.appCode = StringUtils.hasText(appCode) ? StringUtils.trimAllWhitespace(appCode).toUpperCase() : null;
        this.systemSource = systemSource;
        this.certificateData = certificateData;
    }

    public boolean sendToPrint() {
        return this.address != null;
    }

    public boolean sendToApp() {
        return this.appCode != null;
    }

    public boolean isDeliverablePerPost() {
        return true;
    }

    public boolean isBirthdateAfter(final LocalDate date) {
        if (personData == null || date == null || personData.getDateOfBirth() == null) {
            return false;
        }
        final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
        return date.isBefore(parsedDateOfBirth);
    }

    public boolean isBirthdateAfter(final ZonedDateTime date) {
        if (personData == null || date == null || personData.getDateOfBirth() == null) {
            return false;
        }
        final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
        final var swissDate = date.withZoneSameInstant(SWISS_TIMEZONE).toLocalDate();
        return swissDate.isBefore(parsedDateOfBirth);
    }
}

