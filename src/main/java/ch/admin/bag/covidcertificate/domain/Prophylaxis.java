package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sct_vaccines_covid_19")
public class Prophylaxis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    String display;

    boolean active;

    @OneToMany(mappedBy = "prophylaxis")
    List<Vaccine> vaccines;

    public Prophylaxis(
            String code,
            String display,
            boolean active
    ) {

        this.code = code;
        this.display = display;
        this.active = active;
    }
}
