package kz.che.xm.crypto.dto.view;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoRateView {
    @Schema(description = "Currency timestamp")
    private long ts;
    @Schema(description = "Currency type", allowableValues = {"BTC", "DOGE", "ETH", "LTC", "XRP"})
    private CurrencyTypeDto currency;
    @Schema(description = "Currency exchange tate")
    private BigDecimal rate;
}
