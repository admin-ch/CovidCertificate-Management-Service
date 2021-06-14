package ch.admin.bag.covidcertificate.api.valueset;

import java.util.Arrays;

public class AcceptedCantons {

    private enum cantons {
        AG, AI, AR, BE, BL, BS, FR, GE, GL, GR, JU, LU, NE, NW, OW, SG, SH, SO, SZ, TG, TI, UR, VD, VS, ZG, ZH
    }

    public static boolean isAccepted(String canton) {
        return Arrays.stream(cantons.values()).anyMatch(currentCanton -> currentCanton.name().equalsIgnoreCase(canton));
    }
}
