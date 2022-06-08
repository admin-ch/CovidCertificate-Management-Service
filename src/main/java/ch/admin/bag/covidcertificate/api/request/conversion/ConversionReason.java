package ch.admin.bag.covidcertificate.api.request.conversion;

public enum ConversionReason {

    U18_CONVERSION("U18");

    public final String reason;

    ConversionReason(String reason) {
        this.reason = reason;
    }
}
