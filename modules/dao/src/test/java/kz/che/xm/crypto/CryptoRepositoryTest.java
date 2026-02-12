package kz.che.xm.crypto;

import kz.che.xm.crypto.common.Sorter;
import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.repo.CryptoRepository;
import kz.che.xm.crypto.search.CryptoSearch;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.List.of;
import static kz.che.xm.crypto.common.Sorter.DirectionType.DESC;
import static kz.che.xm.crypto.specification.CryptoSpecification.search;
import static kz.che.xm.crypto.specification.SortUtil.convert;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoTestConfig.class)
@ActiveProfiles("test")
@DataJpaTest
public class CryptoRepositoryTest {
    @Autowired
    private CryptoRepository repository;

    @BeforeEach
    void init() {
        repository.saveAll(Arrays.asList(CryptoRateEntity.builder()
                        .ts(1767225600000L)
                        .currency(CurrencyType.BTC)
                        .rate(BigDecimal.valueOf(100))
                        .build(),
                CryptoRateEntity.builder()
                        .ts(1767312000000L)
                        .currency(CurrencyType.BTC)
                        .rate(BigDecimal.valueOf(101))
                        .build(),
                CryptoRateEntity.builder()
                        .ts(1767311000000L)
                        .currency(CurrencyType.ETH)
                        .rate(BigDecimal.valueOf(10))
                        .build(),
                CryptoRateEntity.builder()
                        .ts(1767311000000L)
                        .currency(CurrencyType.DOGE)
                        .rate(BigDecimal.valueOf(1))
                        .build()));
    }

    @Test
    void getBtc() {
        CryptoSearch search = CryptoSearch.builder()
                .currency(CurrencyType.BTC)
                .dateFrom(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .dateTo(ZonedDateTime.parse("2026-01-02T00:00:00Z"))
                .sort(Sorter.builder()
                        .property(of("ts"))
                        .direction(DESC).build())
                .build();
        List<CryptoRateEntity> result = repository.findAll(search(search), convert(search.getSort()));
        assertEquals(2, result.size());
        assertEquals(result.get(0).getTs(), 1767312000000L);
        assertEquals(result.get(0).getCurrency(), CurrencyType.BTC);
        assertEquals(result.get(0).getRate(), BigDecimal.valueOf(101));
        assertEquals(result.get(1).getTs(), 1767225600000L);
        assertEquals(result.get(1).getCurrency(), CurrencyType.BTC);
        assertEquals(result.get(1).getRate(), BigDecimal.valueOf(100));
    }

    @Test
    void getByDate() {
        CryptoSearch search = CryptoSearch.builder()
                .dateFrom(ZonedDateTime.parse("2026-01-01T00:00:00Z"))
                .dateTo(ZonedDateTime.parse("2026-01-01T23:43:20Z"))
                .sort(Sorter.builder()
                        .property(of("ts", "currency"))
                        .direction(DESC).build())
                .build();
        List<CryptoRateEntity> result = repository.findAll(search(search), convert(search.getSort()));
        assertEquals(result.size(), 3);
        assertEquals(result.get(0).getTs(), 1767311000000L);
        assertEquals(result.get(0).getCurrency(), CurrencyType.ETH);
        assertEquals(result.get(0).getRate(), BigDecimal.valueOf(10));
        assertEquals(result.get(1).getTs(), 1767311000000L);
        assertEquals(result.get(1).getCurrency(), CurrencyType.DOGE);
        assertEquals(result.get(1).getRate(), BigDecimal.valueOf(1));
        assertEquals(result.get(2).getTs(), 1767225600000L);
        assertEquals(result.get(2).getCurrency(), CurrencyType.BTC);
        assertEquals(result.get(2).getRate(), BigDecimal.valueOf(100));
    }
}
