package ch.admin.bag.covidcertificate.domain.enums;

public enum EntityType {

    VACCINE("Vaccine"),
    AUTH_HOLDER("AuthHolder"),
    PROPHYLAXIS("Prophylaxis");

    /**
     * The code used to store the value.
     */
    private String code;

    EntityType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
