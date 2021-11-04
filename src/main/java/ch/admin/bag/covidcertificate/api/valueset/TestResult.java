package ch.admin.bag.covidcertificate.api.valueset;


public enum TestResult {
    NEGATIVE("260415000", "Not detected"),
    POSITIVE("260373001", "Detected");

    public final String code;
    public final String display;

    TestResult(String code, String display) {
        this.code = code;
        this.display = display;
    }
}
