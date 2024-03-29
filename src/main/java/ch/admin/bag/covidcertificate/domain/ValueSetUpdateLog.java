package ch.admin.bag.covidcertificate.domain;

import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import ch.admin.bag.covidcertificate.domain.enums.UpdateAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "value_set_update_log")
public class ValueSetUpdateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    @Column(name = "entity_type", nullable = false, columnDefinition = "varchar(50)")
    EntityType entityType;

    @Column(name = "code", nullable = false, columnDefinition = "varchar(50)")
    String code;

    @Column(name = "update_action", nullable = false, columnDefinition = "varchar(50)")
    UpdateAction updateAction;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    LocalDateTime updatedAt;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ValueSetUpdateLog) {
            return Objects.equals(this.id, ((ValueSetUpdateLog) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
