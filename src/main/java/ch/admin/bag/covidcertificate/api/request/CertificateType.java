package ch.admin.bag.covidcertificate.api.request;

public enum CertificateType {
    RECOVERY("recovery"), RECOVERY_RAT("recovery-rat"), TEST("test"), VACCINATION("vaccination"), VACCINATION_TOURIST("vaccination-tourist"), ANTIBODY("antibody"), EXCEPTIONAL("exceptional");

    private final String text;

    /**
     * @param text
     */
    CertificateType(final String text) {
        this.text = text;
    }

    public static CertificateType fromString(String text) {
        for (CertificateType certificateType : CertificateType.values()) {
            if (certificateType.text.equalsIgnoreCase(text)) {
                return certificateType;
            }
        }
        throw new IllegalArgumentException("Text " + text + " is not a valid value.");
    }

    @Override
    public String toString() {
        return text;
    }
}
