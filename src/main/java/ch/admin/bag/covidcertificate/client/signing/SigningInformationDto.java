package ch.admin.bag.covidcertificate.client.signing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@ToString
public class SigningInformationDto {
    private String certificateType;
    private String code;
    private String alias;
    private String certificateAlias;
    private LocalDate validFrom;
    private LocalDate validTo;
    @Setter
    private String calculatedKeyIdentifier;

    public SigningInformationDto(
            String certificateType,
            String code,
            String alias,
            String certificateAlias,
            LocalDate validFrom,
            LocalDate validTo
    ) {
        this.certificateType = certificateType;
        this.code = code;
        this.alias = alias;
        this.certificateAlias = certificateAlias;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public static class SigningInformationDtoBuilder {

        private String certificateType;
        private String code;
        private String alias;
        private String certificateAlias;
        private LocalDate validFrom;
        private LocalDate validTo;

        public SigningInformationDtoBuilder() {
            super();
        }

        public SigningInformationDtoBuilder withCertificateType(String certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public SigningInformationDtoBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public SigningInformationDtoBuilder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public SigningInformationDtoBuilder withCertificateAlias(String certificateAlias) {
            this.certificateAlias = certificateAlias;
            return this;
        }

        public SigningInformationDtoBuilder withValidFrom(LocalDate validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public SigningInformationDtoBuilder withValidTo(LocalDate validTo) {
            this.validTo = validTo;
            return this;
        }

        public SigningInformationDto build() {
            return new SigningInformationDto(
                    this.certificateType,
                    this.code,
                    this.alias,
                    this.certificateAlias,
                    this.validFrom,
                    this.validTo);
        }
    }
}
