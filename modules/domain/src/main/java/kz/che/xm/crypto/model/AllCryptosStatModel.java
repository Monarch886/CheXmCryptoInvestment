package kz.che.xm.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.SortedSet;

/**
 * Wrapper model containing aggregated statistics for multiple cryptocurrencies.
 * <p>
 * The {@link #stats} collection is expected to be a {@link SortedSet} ordered by
 * {@link CryptoStatModel#compareTo(CryptoStatModel)} (typically by normalized value descending).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllCryptosStatModel {

    /**
     * Sorted set of per-currency statistics.
     */
    private SortedSet<CryptoStatModel> stats;
}
