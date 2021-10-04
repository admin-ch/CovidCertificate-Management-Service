package ch.admin.bag.covidcertificate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
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
    private String keyIdentifier;

    public SigningInformation(String certificateType, String code, String alias, String keyIdentifier) {
        this.certificateType = certificateType;
        this.code = code;
        this.alias = alias;
        this.keyIdentifier = keyIdentifier;
    }
}
