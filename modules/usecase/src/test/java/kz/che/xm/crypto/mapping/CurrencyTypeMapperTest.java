package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentError;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = CurrencyTypeMapperTest.TestConfig.class)
class CurrencyTypeMapperTest {

    @jakarta.annotation.Resource
    private CurrencyTypeMapper mapper;

    @Test
    void toModel_shouldMapKnownCurrency() {
        assertEquals(CurrencyType.BTC, mapper.toModel(CurrencyTypeDto.BTC));
    }

    @Test
    void toModel_shouldThrowForUnknownCurrency() {
        CryptoInvestmentException ex = assertThrows(
                CryptoInvestmentException.class,
                () -> mapper.toModel(CurrencyTypeDto.UNKNOWN)
        );
        assertEquals(CryptoInvestmentError.UNKNOWN_CURRENCY, ex.getError());
    }

    @Test
    void toDto_shouldMapKnownCurrency() {
        assertEquals(CurrencyTypeDto.ETH, mapper.toDto(CurrencyType.ETH));
    }

    @Test
    void toDtoString_shouldParse() {
        assertEquals(CurrencyTypeDto.BTC, mapper.toDto("btc"));
        assertEquals(CurrencyTypeDto.UNKNOWN, mapper.toDto("not-a-currency"));
        assertNull(mapper.toDto("  "));
        assertNull(mapper.toDto((String) null));
    }

    @Configuration
    @ComponentScan(basePackages = "kz.che.xm.crypto.mapping")
    static class TestConfig {
    }
}
