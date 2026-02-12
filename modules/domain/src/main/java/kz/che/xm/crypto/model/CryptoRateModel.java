package kz.che.xm.crypto.model;

import kz.che.xm.crypto.type.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A single cryptocurrency rate record in the domain model.
 * <p>
 * Represents the price (rate) of a currency at a specific point in time.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoRateModel {

    /**
     * Timestamp of the rate record.
     * <p>
     * The unit (e.g., epoch millis) depends on the ingestion/persistence layer.
     */
    private long ts;

    /**
     * Currency code for this rate record.
     */
    private CurrencyType currency;

    /**
     * Price/rate value.
     */
    private BigDecimal rate;
}
