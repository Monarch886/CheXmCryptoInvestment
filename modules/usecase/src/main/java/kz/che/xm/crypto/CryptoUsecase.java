package kz.che.xm.crypto;

import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoStatModel;

/**
 * Application use case for retrieving cryptocurrency statistics.
 * <p>
 * This use case provides:
 * <ul>
 *   <li>a list of supported currencies ordered by their normalized volatility</li>
 *   <li>statistics for a particular currency (oldest/newest/min/max/normalized)</li>
 *   <li>the top currency (highest normalized volatility) for a given request parameters</li>
 * </ul>
 * <p>
 * Implementations may use caching for performance. When no data matches the provided search parameters,
 * an implementation may throw a domain-specific runtime exception (e.g. "not found").
 */
public interface CryptoUsecase {

    /**
     * Returns statistics for all currencies matching the request parameters.
     * <p>
     * The result is typically ordered by {@code normalize} value in descending order (top first).
     *
     * @param request search parameters (date range, currency, etc.)
     * @return statistics wrapper for all matched currencies
     * @throws RuntimeException when no data matches the request parameters (implementation-specific)
     */
    AllCryptosStatModel getCurrenciesCacheb(CryptoSearchRequest request);

    /**
     * Returns statistics for a single currency matching the request parameters.
     *
     * @param request search parameters; currency is expected to be provided
     * @return statistics for the requested currency
     * @throws RuntimeException when no data matches the request parameters (implementation-specific)
     */
    CryptoStatModel getStat(CryptoSearchRequest request);

    /**
     * Returns the currency code of the currency with the highest normalized value
     * among the currencies matching the request parameters.
     *
     * @param request search parameters (e.g. date range)
     * @return top currency code (e.g. {@code "BTC"})
     * @throws RuntimeException when no data matches the request parameters (implementation-specific)
     */
    String getTop(CryptoSearchRequest request);
}
