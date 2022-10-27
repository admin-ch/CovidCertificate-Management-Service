package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.api.request.Issuable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

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
@AllArgsConstructor
@Builder
@Table(name = "vaccines_covid_19_names")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String code;

    @Setter
    private String display;

    @Setter
    private boolean active;

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

    private boolean swissMedic;

    private boolean emea;

    private boolean whoEul;

    private String analogVaccine;

    @Setter
    private LocalDateTime modifiedAt;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "auth_holder")
    @Setter
    private AuthHolder authHolder;

    @ManyToOne
    @Setter
    @JoinColumn(name = "prophylaxis")
    private Prophylaxis prophylaxis;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vaccine) {
            return Objects.equals(this.id, ((Vaccine) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public boolean isTouristVaccine() {
        return isWhoEul() && !isEmea() && !isSwissMedic() && !StringUtils.hasText(getAnalogVaccine());
    }
}
