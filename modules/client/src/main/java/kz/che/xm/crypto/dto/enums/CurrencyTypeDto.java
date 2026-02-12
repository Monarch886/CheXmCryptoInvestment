package kz.che.xm.crypto.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Schema(enumAsRef = true)
public enum CurrencyTypeDto {
    BTC,
    DOGE,
    ETH,
    LTC,
    XRP,
    UNKNOWN;

    public static CurrencyTypeDto fromString(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        try {
            return CurrencyTypeDto.valueOf(string.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            log.warn("Unknown currency type: {}. Convert to UNKNOWN", string);
            return UNKNOWN;
        }
    }
}
