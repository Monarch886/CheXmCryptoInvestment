package kz.che.xm.crypto.impl;

import kz.che.xm.crypto.CryptoUsecase;
import kz.che.xm.crypto.adapter.CryptoRepoAdapter;
import kz.che.xm.crypto.common.Sorter;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.mapping.CryptoRateMapper;
import kz.che.xm.crypto.mapping.CryptoSearchMapper;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoRateModel;
import kz.che.xm.crypto.model.CryptoStatModel;
import kz.che.xm.crypto.search.CryptoSearch;
import kz.che.xm.crypto.type.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Runtime.getRuntime;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.util.List.of;
import static kz.che.xm.crypto.common.Sorter.DirectionType.ASC;
import static kz.che.xm.crypto.dto.exception.CryptoInvestmentError.NOT_FOUND;
import static kz.che.xm.crypto.util.CompareUtils.max;
import static kz.che.xm.crypto.util.CompareUtils.min;

/**
 * Default implementation of {@link CryptoUsecase}.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Translate {@link CryptoSearchRequest} into a DAO {@link CryptoSearch}</li>
 *   <li>Apply default sorting (by timestamp, then currency) to guarantee deterministic oldest/newest selection</li>
 *   <li>Fetch data via {@link CryptoRepoAdapter} and map it to domain models via {@link CryptoRateMapper}</li>
 *   <li>Compute per-currency statistics and normalized value</li>
 *   <li>Optionally cache results using Spring Cache annotations</li>
 * </ul>
 * <p>
 * Normalized value is computed as:
 * <pre>{@code
 * (max - min) / min
 * }</pre>
 * with fixed scale and {@link java.math.RoundingMode#HALF_UP}.
 * <p>
 * If no rates are found for the given search parameters, this implementation throws
 * {@link CryptoInvestmentException} with {@link kz.che.xm.crypto.dto.exception.CryptoInvestmentError#NOT_FOUND}.
 */
@Service
@RequiredArgsConstructor
public class CryptoUsecaseImpl implements CryptoUsecase {
    private static final List<String> DEFAULT_PROPERTY_SORT = of("ts", "currency");
    private static final int DIVIDE_CALE = 10;

    private final CryptoRepoAdapter repoAdapter;
    private final CryptoRateMapper mapper;
    private final CryptoSearchMapper searchMapper;

    /**
     * Cached entry point for "all currencies" statistics.
     * Delegates to internal computation method.
     *
     * @param request search parameters (date range, currency, etc.)
     * @return wrapper with per-currency statistics sorted by normalized value (top first)
     * @throws CryptoInvestmentException when no rates match the request parameters
     */
    @Cacheable(cacheNames = "crypto_list", key = "#root.args[0].cacheKey()")
    public AllCryptosStatModel getCurrenciesCacheb(CryptoSearchRequest request) {
        return getCurrencies(request);
    }

    /**
     * Cached entry point for single currency statistics.
     *
     * @param request search parameters; currency is expected to be provided
     * @return computed statistics model
     * @throws CryptoInvestmentException when no rates match the request parameters
     */
    @Cacheable(cacheNames = "crypto_stat", key = "#root.args[0].cacheKey()")
    public CryptoStatModel getStat(CryptoSearchRequest request) {
        return buildStat(getAllBySearch(request), CurrencyType.valueOf(request.getCurrency().name()));
    }

    /**
     * Cached entry point for "top currency" request.
     *
     * @param request search parameters (e.g. date range)
     * @return currency code of the top currency (highest normalized value)
     * @throws CryptoInvestmentException when no rates match the request parameters
     */
    @Cacheable(cacheNames = "crypto_top", key = "#root.args[0].cacheKey()")
    public String getTop(CryptoSearchRequest request) {
        return getCurrencies(request).getStats().getFirst().getCurrency().name();
    }

    /**
     * Computes per-currency statistics for all rates matched by the request.
     */
    private AllCryptosStatModel getCurrencies(CryptoSearchRequest request) {
        List<CryptoRateModel> rates = getAllBySearch(request);
        Map<CurrencyType, List<CryptoRateModel>> byCurrency =
                rates.stream().collect(Collectors.groupingBy(CryptoRateModel::getCurrency));
        return new AllCryptosStatModel(buildStatsParallel(byCurrency));
    }

    /**
     * Builds statistics for a single currency using an already sorted list of rates
     * (first element is "oldest", last element is "newest").
     */
    private CryptoStatModel buildStat(List<CryptoRateModel> rates, CurrencyType currency) {
        CryptoRateModel min = CryptoRateModel.builder()
                .rate(valueOf(MAX_VALUE))
                .build();
        CryptoRateModel max = CryptoRateModel.builder()
                .rate(valueOf(MIN_VALUE))
                .build();
        for (CryptoRateModel crypto : rates) {
            min = min(crypto, min);
            max = max(crypto, max);
        }
        return CryptoStatModel.builder()
                .oldest(rates.getFirst())
                .newest(rates.getLast())
                .min(min)
                .max(max)
                .normalize(countNormalized(min.getRate(), max.getRate()))
                .currency(currency)
                .build();
    }

    /**
     * Fetches and maps rates according to the provided request.
     * <p>
     * Applies default sorting (ASC by {@code ts}, then {@code currency}) to ensure deterministic results.
     *
     * @throws CryptoInvestmentException when repository returns an empty result set
     */
    private List<CryptoRateModel> getAllBySearch(CryptoSearchRequest request) {
        CryptoSearch search = searchMapper.toDao(request);
        search.setSort(Sorter.builder()
                .direction(ASC)
                .property(DEFAULT_PROPERTY_SORT)
                .build());
        List<CryptoRateEntity> rates = repoAdapter.getAllBySearch(search);
        if (rates.isEmpty()) {
            throw new CryptoInvestmentException(NOT_FOUND, "By such parameters currencies found");
        }
        return mapper.toModel(rates);
    }

    /**
     * Builds statistics per currency using a dedicated {@link ForkJoinPool}.
     * The resulting collection is a {@link TreeSet} ordered by {@link CryptoStatModel#compareTo(Object)}.
     */
    private SortedSet<CryptoStatModel> buildStatsParallel(Map<CurrencyType, List<CryptoRateModel>> byCurrency) {
        int parallelism = min(byCurrency.size(), getRuntime().availableProcessors());
        // We use a ForkJoinPool to parallelize statistics calculation across currencies
        // (each currency can be processed independently).
        try (ForkJoinPool pool = new ForkJoinPool(max(1, parallelism))) {
            return pool.submit(() ->
                    byCurrency.entrySet()
                            .parallelStream()
                            .map(e -> buildStat(e.getValue(), e.getKey()))
                            // We collect results into a to automatically keep the output sorted according to
                            // CryptoStatModel#compareTo(CryptoStatModel) (e.g. by {@code normalize, curName} descending).
                            .collect(Collectors.toCollection(TreeSet::new))
            ).join();
        }
    }

    /**
     * Calculates normalized value using fixed scale.
     */
    private BigDecimal countNormalized(BigDecimal min, BigDecimal max) {
        return max.subtract(min).divide(min, DIVIDE_CALE, HALF_UP);
    }
}
