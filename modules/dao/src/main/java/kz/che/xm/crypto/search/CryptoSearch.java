package kz.che.xm.crypto.search;

import kz.che.xm.crypto.common.Sorter;
import kz.che.xm.crypto.type.CurrencyType;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * DAO-layer search parameters for querying crypto rate records.
 * <p>
 * This object is typically produced from an API request DTO (e.g. {@code CryptoSearchRequest})
 * and then used by the repository adapter/specification layer to build database queries.
 * <p>
 * Null values are treated as "no filtering" for the corresponding field.
 */
@Data
@Builder
public class CryptoSearch {

    /**
     * Start of the date/time window (inclusive). When {@code null}, the lower bound is not applied.
     */
    private ZonedDateTime dateFrom;

    /**
     * End of the date/time window (inclusive). When {@code null}, the upper bound is not applied.
     */
    private ZonedDateTime dateTo;

    /**
     * Currency to filter by. When {@code null}, the currency filter is not applied.
     */
    private CurrencyType currency;

    /**
     * Sorting configuration for the query. May be {@code null} if the caller relies on a default sort.
     */
    private Sorter sort;
}
