package ch.admin.bag.covidcertificate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "display_name_modification")
public class DisplayNameModification {

    @Id
    @Column(name = "code", nullable = false, columnDefinition = "varchar(50)")
    private String code;

    @Column(name = "display", nullable = false, columnDefinition = "varchar(100)")
    private String display;

    @Column(name = "entity_type", nullable = false, columnDefinition = "varchar(50)")
    private EntityType entityType;
}
