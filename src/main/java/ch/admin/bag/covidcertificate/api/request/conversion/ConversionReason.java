package ch.admin.bag.covidcertificate.api.request.conversion;

public enum ConversionReason {

    VACCINATION_CONVERSION("VACCINATION_CONVERSION");

    public final String reason;

    ConversionReason(String reason) {
        this.reason = reason;
    }
}
