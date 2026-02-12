package kz.che.xm.crypto.config;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CurrencyTypeDtoConverter implements Converter<String, CurrencyTypeDto> {

    @Override
    public CurrencyTypeDto convert(String source) {
        return CurrencyTypeDto.fromString(source);
    }
}
