package ch.admin.bag.covidcertificate.api.valueset;

public class AcceptedLanguages {
    private AcceptedLanguages() {
    }

    public static final String DE = "de";
    public static final String IT = "it";
    public static final String FR = "fr";
    public static final String RM = "rm";
    public static final String EN = "en";

    public static boolean isAcceptedLanguage(String language) {
        return language != null && (language.equals(DE) || language.equals(IT) || language.equals(FR) || language.equals(RM));
    }
}
