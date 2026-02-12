package kz.che.xm.crypto.dto.view;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoStatView {
    @Schema(description = "Oldest rate for period")
    private CryptoRateView oldest;
    @Schema(description = "Newest rate for period")
    private CryptoRateView newest;
    @Schema(description = "Minimal rate for period")
    private CryptoRateView min;
    @Schema(description = "Maximal rate for period")
    private CryptoRateView max;
    @Schema(description = "Currency")
    private CurrencyTypeDto currency;
}
