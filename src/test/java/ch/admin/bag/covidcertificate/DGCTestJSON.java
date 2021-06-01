package ch.admin.bag.covidcertificate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class DGCTestJSON {
    @JsonProperty("JSON")
    private Object json;
    @JsonProperty("CBOR")
    private String cbor;
    @JsonProperty("COSE")
    private String cose;
    @JsonProperty("COMPRESSED")
    private String compressed;
    @JsonProperty("BASE45")
    private String base45;
    @JsonProperty("PREFIX")
    private String prefix;
    @JsonProperty("2DCODE")
    private String code2d;
    @JsonProperty("TESTCTX")
    private DGCTestJSONTestContext testContext;
    @JsonProperty("EXPECTEDRESULTS")
    private DGCTestJSONExpectedResults expectedResults;
}
