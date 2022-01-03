package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateHelper;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionalCertificateCsvBean extends CertificateCsvBean {

    @CsvBindByName(column = "validFrom")
    private String validFrom;
    @CsvBindByName(column = "attestationIssuer")
    private String attestationIssuer;

    @Override
    public ExceptionalCertificateCreateDto mapToCreateDto() {
        ExceptionalCertificateDataDto dataDto = new ExceptionalCertificateDataDto(
                DateHelper.parse(this.validFrom, INVALID_SAMPLE_DATE_TIME),
                attestationIssuer
        );
        return super.mapToCreateDto(dataDto);
    }
}
