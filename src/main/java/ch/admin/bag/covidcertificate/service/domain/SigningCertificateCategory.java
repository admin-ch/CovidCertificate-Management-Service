package ch.admin.bag.covidcertificate.service.domain;

public enum SigningCertificateCategory {
    VACCINATION("vaccination"),
    VACCINATION_TOURIST_CH("vaccination_tourist_ch"),
    RECOVERY_CH("recovery_ch"),
    RECOVERY_NON_CH("recovery_non_ch"),
    RECOVERY_RAT_CH("recovery_rat_ch"),
    TEST("test"),
    ANTIBODY_CH("antibody_ch"),
    EXCEPTIONAL_CH("exceptional_ch");

    public final String value;

    SigningCertificateCategory(String value) {
        this.value = value;
    }
}
