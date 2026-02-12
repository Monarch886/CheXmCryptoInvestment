package kz.che.xm.crypto.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CryptoInvestmentError {
    // ===== COMMON =====
    PLATFORM_SERVICE_ERROR(500, "Unexpected error while getting response from platform services"),

    // ===== SEC =====
    TOO_MANY_REQUESTS(429, "Too many requests, try again later. "),

    // ===== CRYPTO =====
    UNKNOWN_CURRENCY(400, "Unknown currency."),
    REQUIRED_PARAM_NOT_FOUND(400, "Need required param."),
    NOT_FOUND(404, "Currency not found.");

    /**
     * http error status code.
     */
    private final int status;

    /**
     * Default error message.
     */
    private final String message;
}
