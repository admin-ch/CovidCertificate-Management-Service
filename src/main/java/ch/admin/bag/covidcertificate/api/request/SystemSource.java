package ch.admin.bag.covidcertificate.api.request;

public enum SystemSource {
    WebUI("UI"),
    CsvUpload("UI"),
    ApiGateway("API"),
    ApiPlatform("API"),
    Conversion("CCC"), // CCC = Covid Certificate Conversion

    RevocationListReduction("RLR"); // RLR = Revocation List Reduction
    public final String category;

    SystemSource(final String category) {
        this.category = category;
    }
}
