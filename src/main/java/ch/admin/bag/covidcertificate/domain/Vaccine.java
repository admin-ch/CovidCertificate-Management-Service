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
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "vaccines_covid_19_names")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String code;

    private String display;

    private boolean active;

    /**
     * This attribute is a simple boolean based on it's database value
     *
     * @deprecated This attribute is legacy and should be replaced by issuable.
     * <p> Use {@link Vaccine#issuable} instead.
     */
    @Deprecated(since = "2.5.8")
    private boolean chIssuable;

    /**
     * This attribute tells us if a vaccine is issuable in CH_ONLY,
     * CH_AND_ABROAD or ABROAD_ONLY and it is based on its stored enum value
     * in the database.
     */
    private Issuable issuable;

    private int vaccineOrder;

    private boolean webUiSelectable;

    private boolean apiGatewaySelectable;

    private boolean apiPlatformSelectable;

    private boolean whoEul;

    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "auth_holder")
    @Setter
    private AuthHolder authHolder;

    @ManyToOne
    @Setter
    @JoinColumn(name = "prophylaxis")
    private Prophylaxis prophylaxis;

    public Vaccine(
            String code,
            String display,
            boolean active,
            boolean chIssuable,
            Issuable issuable,
            int vaccineOrder,
            boolean webUiSelectable,
            boolean apiGatewaySelectable,
            boolean apiPlatformSelectable,
            boolean whoEul
    ) {
        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = chIssuable;
        this.issuable = issuable;
        this.vaccineOrder = vaccineOrder;
        this.webUiSelectable = webUiSelectable;
        this.apiGatewaySelectable = apiGatewaySelectable;
        this.apiPlatformSelectable = apiPlatformSelectable;
        this.whoEul = whoEul;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vaccine) {
            return Objects.equals(this.code, ((Vaccine) obj).getCode());
        }
        return false;
    }
}
