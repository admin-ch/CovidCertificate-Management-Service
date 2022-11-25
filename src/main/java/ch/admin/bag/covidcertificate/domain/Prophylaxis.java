package ch.admin.bag.covidcertificate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sct_vaccines_covid_19")
public class Prophylaxis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    @Setter
    String display;

    @Setter
    boolean active;

    @Setter
    LocalDateTime modifiedAt;

    LocalDateTime createdAt;

    @OneToMany(mappedBy = "prophylaxis")
    List<Vaccine> vaccines;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Prophylaxis) {
            return Objects.equals(this.id, ((Prophylaxis) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
