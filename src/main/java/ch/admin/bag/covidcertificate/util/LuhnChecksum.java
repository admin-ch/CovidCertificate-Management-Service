package ch.admin.bag.covidcertificate.util;

public final class LuhnChecksum {

    private static final String ALPHABET = "1234567890ABCDEFHKMNPRSTUWXYZ";
    private static final int N = ALPHABET.length();
    public static final int TRANSFER_CODE_LEN = 9;

    private LuhnChecksum() {
        throw new AssertionError();
    }

    public static boolean validateCheckCharacter(String expression) {
        return checkAlgorithm(expression);
    }

    private static boolean checkAlgorithm(String expression) {
        return computeCheckCharacter(expression.substring(0, 8)).equals(expression.substring(8));
    }

    private static String computeCheckCharacter(String code) {
        String reversed = new StringBuilder(code).reverse().toString();
        int sum = 0;
        int factor = 2;
        for (int i = 0; i < reversed.length(); ++i) {
            char el = reversed.charAt(i);
            int idx = ALPHABET.indexOf(el);
            if (idx < 0) {
                return "0";
            }

            int toAdd = factor * idx;
            sum += toAdd / N + toAdd % N;
            factor = factor == 1 ? 2 : 1;
        }

        int checkChar = N - sum % N;
        if (checkChar == N) {
            checkChar = 0;
        }

        return Character.toString(ALPHABET.charAt(checkChar));
    }
}