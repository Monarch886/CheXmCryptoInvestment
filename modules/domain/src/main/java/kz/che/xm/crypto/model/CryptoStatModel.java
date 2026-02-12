package kz.che.xm.crypto.model;

import kz.che.xm.crypto.type.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Aggregated statistics for a cryptocurrency within a selected time window.
 * <p>
 * Contains:
 * <ul>
 *   <li>oldest and newest rate records (by timestamp)</li>
 *   <li>minimum and maximum rate records (by rate value)</li>
 *   <li>normalized value used for ranking currencies</li>
 * </ul>
 * <p>
 * Instances are {@link Comparable} and are sorted by {@link #normalize} in descending order
 * (higher normalize = "greater", appears first in sorted sets).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoStatModel implements Comparable<CryptoStatModel> {

    /**
     * The oldest rate record in the window (lowest timestamp).
     */
    private CryptoRateModel oldest;

    /**
     * The newest rate record in the window (highest timestamp).
     */
    private CryptoRateModel newest;

    /**
     * Rate record with the minimal {@code rate} value in the window.
     */
    private CryptoRateModel min;

    /**
     * Rate record with the maximal {@code rate} value in the window.
     */
    private CryptoRateModel max;

    /**
     * Currency code these statistics belong to.
     */
    private CurrencyType currency;

    /**
     * Normalized value used to rank currencies, typically computed as {@code (max - min) / min}.
     */
    private BigDecimal normalize;

    @Override
    public int compareTo(CryptoStatModel another) {
        int compare = another.normalize.compareTo(normalize);
        if (compare == 0) {
            return currency.name().compareTo(another.getCurrency().name());
        }
        return compare;
    }
}
