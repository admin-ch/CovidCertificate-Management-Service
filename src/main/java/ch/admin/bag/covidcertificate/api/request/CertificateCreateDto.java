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

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_DELIVERY_METHOD;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_APP_CODE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_LANGUAGE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_PRINT_FOR_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.NO_PERSON_DATA;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CertificateCreateDto {
    @JsonUnwrapped
    private CovidCertificatePersonDto personData;
    private String language;
    private CovidCertificateAddressDto address;
    @JsonDeserialize(using = StringNotEmptyToUppercaseElseNullDeserializer.class)
    private String appCode;
    private SystemSource systemSource;
    private String userExtId;

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

    public void validate() {
        if (systemSource == null) {
            throw new IllegalStateException("mandatory attribute systemSource is missing. Check Request implementation.");
        }
        if (personData == null) {
            throw new CreateCertificateException(NO_PERSON_DATA);
        } else {
            personData.validate();
        }
        if (!AcceptedLanguages.isAcceptedLanguage(language)) {
            throw new CreateCertificateException(INVALID_LANGUAGE);
        }
        this.validateDeliveryMethod();
    }

    private void validateDeliveryMethod() {
        if (this.sendToPrint() && StringUtils.hasText(this.appCode)) {
            throw new CreateCertificateException(DUPLICATE_DELIVERY_METHOD);
        } else if (this.sendToPrint()) {
            if (!this.isDeliverablePerPost()) {
                throw new CreateCertificateException(INVALID_PRINT_FOR_TEST);
            }
            this.address.validate();
        } else if (this.sendToApp() && !org.apache.commons.lang3.StringUtils.isAlphanumeric(this.appCode)) {
            throw new CreateCertificateException(INVALID_APP_CODE);
        }
    }

    public boolean isDeliverablePerPost() {
        return true;
    }

    public boolean isBirthdateAfter(final LocalDate date) {
        if (personData == null || date == null) {
            return false;
        }
        final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
        return date.isBefore(parsedDateOfBirth);
    }

    public boolean isBirthdateAfter(final ZonedDateTime date) {
        if (personData == null || date == null) {
            return false;
        }
        final var parsedDateOfBirth = DateHelper.parseDateOfBirth(personData.getDateOfBirth());
        final var swissDate = date.withZoneSameInstant(SWISS_TIMEZONE).toLocalDate();
        return swissDate.isBefore(parsedDateOfBirth);
    }
}

