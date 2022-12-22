package ch.admin.bag.covidcertificate.api.request;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RevocationCsvBean {

    public static final String STATUS_OK = "OK";

    @CsvBindByName(column = "uvci")
    private String uvci;
    @CsvBindByName(column = "fraud")
    private Boolean fraud;
    @CsvBindByName(column = "status")
    @Setter
    private String status;

    public UvciForRevocationDto mapToDto() {
        return new UvciForRevocationDto(
                uvci,
                fraud
        );
    }
}
