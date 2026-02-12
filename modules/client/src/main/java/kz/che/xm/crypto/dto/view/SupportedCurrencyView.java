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
public class SupportedCurrencyView {
    @Schema(description = "Currency type", allowableValues = {"BTC", "DOGE", "ETH", "LTC", "XRP"})
    private CurrencyTypeDto currency;
}
