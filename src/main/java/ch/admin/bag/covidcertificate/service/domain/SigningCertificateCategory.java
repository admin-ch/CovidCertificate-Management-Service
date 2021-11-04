package ch.admin.bag.covidcertificate.service.domain;

public enum SigningCertificateCategory {
    VACCINATION("vaccination"),
    RECOVERY_CH("recovery_ch"),
    RECOVERY_NON_CH("recovery_non_ch"),
    TEST("test"),
    ANTIBODY_CH("antibody_ch");

    public final String value;

    SigningCertificateCategory(String value) {
        this.value = value;
    }
}
