package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.api.request.Issuable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "vaccines_covid_19_names")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    String display;

    boolean active;

    /**
     * This attribute is a simple boolean based on it's database value
     *
     * @deprecated This attribute is legacy and should be replaced by issuable.
     * <p> Use {@link Vaccine#issuable} instead.
     */
    @Deprecated(since = "2.5.8")
    boolean chIssuable;

    /**
     * This attribute tells us if a vaccine is issuable in CH_ONLY,
     * CH_AND_ABROAD or ABROAD_ONLY and it is based on its stored enum value
     * in the database.
     */
    Issuable issuable;

    boolean webUiSelectable;

    boolean apiGatewaySelectable;

    boolean apiPlatformSelectable;

    LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "auth_holder")
    @Setter
    AuthHolder authHolder;

    @ManyToOne
    @Setter
    @JoinColumn(name = "prophylaxis")
    Prophylaxis prophylaxis;

    public Vaccine(
            String code,
            String display,
            boolean active,
            boolean chIssuable,
            Issuable issuable,
            boolean webUiSelectable,
            boolean apiGatewaySelectable,
            boolean apiPlatformSelectable
    ) {

        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = chIssuable;
        this.issuable = issuable;
        this.webUiSelectable = webUiSelectable;
        this.apiGatewaySelectable = apiGatewaySelectable;
        this.apiPlatformSelectable = apiPlatformSelectable;
    }
}
