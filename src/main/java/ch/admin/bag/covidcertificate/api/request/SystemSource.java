package ch.admin.bag.covidcertificate.api.request;

public enum SystemSource {
    WebUI("UI"),
    CsvUpload("UI"),
    ApiGateway("API"),
    ApiPlatform("API");

    public final String category;

    SystemSource(final String category) {
        this.category = category;
    }
}
