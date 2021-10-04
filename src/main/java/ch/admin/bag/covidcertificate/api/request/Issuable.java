package ch.admin.bag.covidcertificate.api.request;

public enum Issuable {

    CH_AND_ABROAD("ch_and_abroad"),
    CH_ONLY("ch_only"),
    ABROAD_ONLY("abroad_only"),
    UNDEFINED("undefined");

    /**
     * The code used to store the value.
     */
    private String code;

    Issuable(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
