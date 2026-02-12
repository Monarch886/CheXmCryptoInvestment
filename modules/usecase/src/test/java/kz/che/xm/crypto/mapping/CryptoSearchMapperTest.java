package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.search.CryptoSearch;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = CryptoSearchMapperTest.TestConfig.class)
class CryptoSearchMapperTest {

    @jakarta.annotation.Resource
    private CryptoSearchMapper mapper;

    @Test
    void toDao_shouldMapFields_andIgnoreSort() {
        ZonedDateTime from = ZonedDateTime.parse("2022-01-01T00:00:00+05:00");
        ZonedDateTime to = ZonedDateTime.parse("2022-01-02T00:00:00+05:00");

        CryptoSearchRequest request = CryptoSearchRequest.builder()
                .dateFrom(from)
                .dateTo(to)
                .currency(CurrencyTypeDto.BTC)
                .build();

        CryptoSearch dao = mapper.toDao(request);

        assertNotNull(dao);
        assertEquals(from, dao.getDateFrom());
        assertEquals(to, dao.getDateTo());
        assertEquals(CurrencyType.BTC, dao.getCurrency());
        assertNull(dao.getSort(), "sort must be ignored by mapper (set by service/usecase)");
    }

    @Test
    void toDao_shouldAllowNulls() {
        CryptoSearch dao = mapper.toDao(CryptoSearchRequest.builder().build());
        assertNotNull(dao);
        assertNull(dao.getDateFrom());
        assertNull(dao.getDateTo());
        assertNull(dao.getCurrency());
        assertNull(dao.getSort());
    }

    @Configuration
    @ComponentScan(basePackages = "kz.che.xm.crypto.mapping")
    static class TestConfig {
    }
}
