package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "vaccines_covid_19_names")
@SecondaryTables({
    @SecondaryTable(name = "sct_vaccines_covid_19", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
    @SecondaryTable(name = "vaccines_covid_19_auth_holders", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
})
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    String display;

    boolean active;

    boolean chIssuable;

    LocalDateTime modifiedAt;

    @Column(name = "code", table = "sct_vaccines_covid_19")
    String prophylaxisCode;

    @Column(name = "display", table = "sct_vaccines_covid_19")
    String prophylaxisDisplayName;

    @Column(name = "active", table = "sct_vaccines_covid_19")
    boolean prophylaxisActive;

    @Column(name = "code", table = "vaccines_covid_19_auth_holders")
    String authHolderCode;

    @Column(name = "display", table = "vaccines_covid_19_auth_holders")
    String authHolderDisplayName;

    @Column(name = "active", table = "vaccines_covid_19_auth_holders")
    boolean authHolderActive;

    public Vaccine(
            String code,
            String display,
            boolean active,
            boolean chIssuable,
            String prophylaxisCode,
            String prophylaxisDisplayName,
            boolean prophylaxisActive,
            String authHolderCode,
            String authHolderDisplayName,
            boolean authHolderActive) {

        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = chIssuable;
        this.prophylaxisCode = prophylaxisCode;
        this.prophylaxisDisplayName = prophylaxisDisplayName;
        this.prophylaxisActive = prophylaxisActive;
        this.authHolderCode = authHolderCode;
        this.authHolderDisplayName = authHolderDisplayName;
        this.authHolderActive = authHolderActive;
    }
}
