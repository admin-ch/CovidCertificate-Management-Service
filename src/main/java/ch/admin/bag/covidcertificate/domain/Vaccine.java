package ch.admin.bag.covidcertificate.domain;

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
    private UUID id;

    private String code;

    private String display;

    private boolean active;

    private boolean chIssuable;

    private boolean swissMedic;

    private boolean emea;

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
            boolean swissMedic
    ) {

        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = chIssuable;
        this.swissMedic = swissMedic;
    }
}
