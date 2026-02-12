package kz.che.xm.crypto.dto.reqres;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CryptoSearchRequest {
    @Schema(description = "Filter by start date", requiredMode = NOT_REQUIRED)
    private ZonedDateTime dateFrom;
    @Schema(description = "Filter by last date", requiredMode = NOT_REQUIRED)
    private ZonedDateTime dateTo;
    @Schema(implementation = CurrencyTypeDto.class, description = "Filter by currency", requiredMode = NOT_REQUIRED)
    private CurrencyTypeDto currency;

    public String cacheKey() {
        return String.format("%s|%s|%s", dateFrom, dateTo, currency);
    }
}
