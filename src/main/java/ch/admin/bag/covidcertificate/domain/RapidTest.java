package ch.admin.bag.covidcertificate.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    ZonedDateTime validUntil;

    public RapidTest(String code, String display, boolean active, LocalDateTime modifiedAt, ZonedDateTime validUntil) {
        this.code = code;
        this.display = display;
        this.active = active;
        this.chIssuable = true;
        this.modifiedAt = modifiedAt;
        this.validUntil = validUntil;
    }

    public void update(String display, boolean active, LocalDateTime modifiedAt, ZonedDateTime validUntil) {
        this.display = display;
        this.active = active;
        this.modifiedAt = modifiedAt;
        this.validUntil = validUntil;
    }

    public void deactivate(LocalDateTime modifiedAt) {
        this.active = false;
        this.modifiedAt = modifiedAt;
    }
}
