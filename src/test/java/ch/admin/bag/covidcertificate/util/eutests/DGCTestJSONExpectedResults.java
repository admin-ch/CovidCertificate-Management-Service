package ch.admin.bag.covidcertificate.util.eutests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class DGCTestJSONExpectedResults {
    @JsonProperty("EXPECTEDVALIDOBJECT")
    private Boolean expectedValidObject;
    @JsonProperty("EXPECTEDSCHEMAVALIDATION")
    private Boolean expectedSchemaValidation;
    @JsonProperty("EXPECTEDENCODE")
    private Boolean expectedEncode;
    @JsonProperty("EXPECTEDDECODE")
    private Boolean expectedDecode;
    @JsonProperty("EXPECTEDVERIFY")
    private Boolean expectedVerify;
    @JsonProperty("EXPECTEDCOMPRESSION")
    private Boolean expectedCompression;
    @JsonProperty("EXPECTEDKEYUSAGE")
    private Boolean expectedKeyUsage;
    @JsonProperty("EXPECTEDUNPREFIX")
    private Boolean expectedUnPrefix;
    @JsonProperty("EXPECTEDVALIDJSON")
    private Boolean expectedValidJSON;
    @JsonProperty("EXPECTEDB45DECODE")
    private Boolean expectedB45Decode;
    @JsonProperty("EXPECTEDPICTUREDECODE")
    private Boolean expectedPictureDecode;
    @JsonProperty("EXPECTEDEXPIRATIONCHECK")
    private Boolean expectedExpirationCheck;
}
