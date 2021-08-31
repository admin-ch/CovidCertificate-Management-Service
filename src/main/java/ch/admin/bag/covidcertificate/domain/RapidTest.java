package ch.admin.bag.covidcertificate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Table(name = "covid_19_lab_test_manufacturer_and_name")
public class RapidTest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;

    String code;

    String display;

    boolean active;

    boolean chIssuable;

    LocalDateTime modifiedAt;

    public RapidTest(String code, String display, boolean active, LocalDateTime modifiedAt) {
        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = false;
        this.modifiedAt = modifiedAt;
    }

    public void update(String display, boolean active, LocalDateTime modifiedAt) {
        this.display = display;
        this.active = active;
        this.modifiedAt = modifiedAt;
    }

    public void deactivate(LocalDateTime modifiedAt) {
        this.active = false;
        this.modifiedAt = modifiedAt;
    }
}
