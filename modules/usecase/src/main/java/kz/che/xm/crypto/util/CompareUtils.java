package kz.che.xm.crypto.util;

import kz.che.xm.crypto.model.CryptoRateModel;

public class CompareUtils {
    public static CryptoRateModel max(CryptoRateModel a, CryptoRateModel b) {
        return a.getRate().compareTo(b.getRate()) > 0 ? a : b;
    }

    public static CryptoRateModel min(CryptoRateModel a, CryptoRateModel b) {
        return a.getRate().compareTo(b.getRate()) < 0 ? a : b;
    }
}
