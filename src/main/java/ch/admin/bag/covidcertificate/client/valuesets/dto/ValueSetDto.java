package ch.admin.bag.covidcertificate.client.valuesets.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ValueSetDto {

    private String display;

    private boolean active;

}
