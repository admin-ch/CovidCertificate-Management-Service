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

    @AssertFalse(message = "Print is not available for test certificates")
    public boolean isInvalidPrint() {
        if (!this.sendToPrint() || !StringUtils.hasText(this.appCode)) {
            if (this.sendToPrint() && !this.isDeliverablePerPost()) {
                return true;
            }
            Validation.buildDefaultValidatorFactory().usingContext()
        }
        return false;
    }

    @AssertFalse(message = "App code is in an invalid format.")
    public boolean isInvalidAppCode() {
        if (!this.sendToPrint() || !StringUtils.hasText(this.appCode)) {
            if (this.sendToApp() && !org.apache.commons.lang3.StringUtils.isAlphanumeric(this.appCode)) {
                return true;
            }
        }
        return false;
    }

    protected CertificateCreateDto(CovidCertificatePersonDto personData, String language, CovidCertificateAddressDto address, String appCode, SystemSource systemSource) {
        this.personData = personData;
        this.language = language;
        this.address = address;
        this.appCode = StringUtils.hasText(appCode) ? StringUtils.trimAllWhitespace(appCode).toUpperCase() : null;
        this.systemSource = systemSource;
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
        try {
            final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
            return date.isBefore(parsedDateOfBirth);
        } catch (CreateCertificateException e) {
            return false;
        }
    }

    public boolean isBirthdateAfter(final ZonedDateTime date) {
        if (personData == null || date == null || personData.getDateOfBirth() == null) {
            return false;
        }
        try {
            final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
            final var swissDate = date.withZoneSameInstant(SWISS_TIMEZONE).toLocalDate();
            return swissDate.isBefore(parsedDateOfBirth);
        } catch (CreateCertificateException e) {
            return false;
        }
    }
}

