package ch.admin.bag.covidcertificate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "covid_19_lab_test_manufacturer_and_name")
public class RapidTest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    String code;
    String display;
    Boolean active;
    @Column(insertable = false)
    Boolean chIssuable;
    @Column(insertable = false)
    LocalDateTime modifiedAt;

    public RapidTest(String code, String display, Boolean active) {
        this.code = code;
        this.display = display;
        this.active = active;
    }
}
