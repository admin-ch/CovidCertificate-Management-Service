package ch.admin.bag.covidcertificate.domain;

public enum UpdateAction {

    UPDATE("update"),
    DELETE("delete"),
    NEW("new");

    /**
     * The code used to store the value.
     */
    private String code;

    UpdateAction(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
