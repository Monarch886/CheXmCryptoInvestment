package kz.che.xm.crypto.specification;

import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.enity.CryptoRateEntity_;
import kz.che.xm.crypto.search.CryptoSearch;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.isNull;

/**
 * Specifications for querying {@link CryptoRateEntity} records.
 * <p>
 * Provides a single entry point ({@link #search(CryptoSearch)}) that combines:
 * <ul>
 *   <li>date range filtering by timestamp (inclusive)</li>
 *   <li>currency filtering</li>
 * </ul>
 * <p>
 * Null values in {@link CryptoSearch} are treated as "no filter" for the corresponding criterion.
 */
public class CryptoSpecification extends CommonSpecification<CryptoRateEntity> {

    /**
     * Builds a combined specification based on the provided {@link CryptoSearch} filters.
     * <p>
     * Date range is built using {@link CommonSpecification#betweenFilter(String, Comparable, Comparable)}
     * on the {@code ts} attribute, where {@link java.time.ZonedDateTime} bounds are converted to epoch millis.
     *
     * @param search search parameters (dateFrom/dateTo/currency)
     * @return combined specification for querying {@link CryptoRateEntity}
     */
    public static Specification<CryptoRateEntity> search(CryptoSearch search) {
        Specification<CryptoRateEntity> filteredByDate = betweenFilter(
                CryptoRateEntity_.TS,
                isNull(search.getDateFrom()) ? null : search.getDateFrom().toInstant().toEpochMilli(),
                isNull(search.getDateTo()) ? null : search.getDateTo().toInstant().toEpochMilli());

        Specification<CryptoRateEntity> filterByCur = equalFilter(
                CryptoRateEntity_.CURRENCY, search.getCurrency());

        return filteredByDate.and(filterByCur);
    }
}
