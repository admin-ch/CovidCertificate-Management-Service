package ch.admin.bag.covidcertificate.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "signing_information")
public class SigningInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String certificateType;
    private String code;
    private String alias;
    @Deprecated
    private String keyIdentifier;
    private String certificateAlias;
    private LocalDate validFrom;
    private LocalDate validTo;

    public SigningInformation(String certificateType, String code, String alias, String keyIdentifier, String certificateAlias, LocalDate validFrom, LocalDate validTo) {
        this.certificateType = certificateType;
        this.code = code;
        this.alias = alias;
        this.keyIdentifier = keyIdentifier;
        this.certificateAlias = certificateAlias;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
}
