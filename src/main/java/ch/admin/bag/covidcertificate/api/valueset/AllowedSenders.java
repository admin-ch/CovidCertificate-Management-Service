package ch.admin.bag.covidcertificate.api.valueset;

import java.util.Arrays;

public class AllowedSenders {

    private enum cantons {
        // MI is mapped to the address of the swiss military
        AG, AI, AR, BE, BL, BS, FR, GE, GL, GR, JU, LU, NE, NW, OW, SG, SH, SO, SZ, TG, TI, UR, VD, VS, ZG, ZH, MI
    }

    public static boolean isAccepted(String canton) {
        return Arrays.stream(cantons.values()).anyMatch(currentCanton -> currentCanton.name().equalsIgnoreCase(canton));
    }
}
