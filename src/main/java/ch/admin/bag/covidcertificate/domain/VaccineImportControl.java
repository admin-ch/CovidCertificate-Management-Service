package ch.admin.bag.covidcertificate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vaccine_import_control")
public class VaccineImportControl {
    @Id
    @Column(name = "import_version", nullable = false, columnDefinition = "varchar(50)")
    private String importVersion;

    @Column(name = "import_date", nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDate importDate;

    @Column(name = "done", nullable = false, columnDefinition = "boolean default false")
    @Setter
    private boolean done;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VaccineImportControl) {
            return Objects.equals(this.importVersion, ((VaccineImportControl) obj).getImportVersion());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.importVersion);
    }
}
