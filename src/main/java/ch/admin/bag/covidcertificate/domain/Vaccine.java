package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Table(name = "vaccines_covid_19_names")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    @Setter
    String display;

    @Setter
    boolean active;

    /**
     * This attribute tells us if a vaccine is issuable in CH_ONLY,
     * CH_AND_ABROAD or ABROAD_ONLY and it is based on its stored enum value
     * in the database.
     */
    Issuable issuable;

    int vaccineOrder;

    boolean webUiSelectable;

    boolean apiGatewaySelectable;

    boolean apiPlatformSelectable;

    boolean swissMedic;

    boolean emea;

    boolean whoEul;

    String analogVaccine;

    @Setter
    LocalDateTime modifiedAt;

    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "auth_holder")
    @Setter
    AuthHolder authHolder;

    @ManyToOne
    @Setter
    @JoinColumn(name = "prophylaxis")
    Prophylaxis prophylaxis;

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
