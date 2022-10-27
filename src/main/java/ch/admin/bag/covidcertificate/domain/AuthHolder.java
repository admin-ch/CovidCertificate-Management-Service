package ch.admin.bag.covidcertificate.domain;

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
@Table(name = "vaccines_covid_19_auth_holders")
public class AuthHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    @Setter
    String display;

    @Setter
    boolean active;

    @Setter
    private LocalDateTime modifiedAt;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "authHolder")
    List<Vaccine> vaccines;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AuthHolder) {
            return Objects.equals(this.id, ((AuthHolder) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
