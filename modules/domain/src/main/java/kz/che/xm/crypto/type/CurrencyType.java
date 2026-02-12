package kz.che.xm.crypto.type;

/**
 * Supported cryptocurrency codes within the domain layer.
 * <p>
 * This enum represents the canonical set of currencies the system can operate with.
 * Values are used across persistence, domain models, and business logic.
 * <p>
 * When introducing a new currency, add it here and also update the corresponding API enum (DTO layer).
 */
public enum CurrencyType {
    BTC,
    DOGE,
    ETH,
    LTC,
    XRP,

    /**
     * Fallback value for unknown/unsupported currencies.
     * <p>
     * Note: depending on the mapping layer, {@code UNKNOWN} may be rejected and turned into an error.
     */
    UNKNOWN
}
