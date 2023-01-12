package ch.admin.bag.covidcertificate.domain.enums;

public enum Delivery {
    PRINT_BILLABLE("print_billable"),
    PRINT_NON_BILLABLE("print_non_billable"),
    APP("app"),
    OTHER("other");

    /**
     * The code used to store the value.
     */
    private String code;

    Delivery(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
