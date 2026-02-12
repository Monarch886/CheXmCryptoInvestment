package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.view.AllSupportedCryptosView;
import kz.che.xm.crypto.dto.view.CryptoStatView;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoRateModel;
import kz.che.xm.crypto.model.CryptoStatModel;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(classes = CryptoStatMapperTest.TestConfig.class)
class CryptoStatMapperTest {

    @jakarta.annotation.Resource
    private CryptoStatMapper mapper;

    @Test
    void toView_shouldMapCryptoStatModel() {
        CryptoRateModel oldest = CryptoRateModel.builder()
                .ts(1L)
                .currency(CurrencyType.BTC)
                .rate(new BigDecimal("10"))
                .build();

        CryptoRateModel newest = CryptoRateModel.builder()
                .ts(3L)
                .currency(CurrencyType.BTC)
                .rate(new BigDecimal("30"))
                .build();

        CryptoRateModel min = CryptoRateModel.builder()
                .ts(2L)
                .currency(CurrencyType.BTC)
                .rate(new BigDecimal("5"))
                .build();

        CryptoRateModel max = CryptoRateModel.builder()
                .ts(4L)
                .currency(CurrencyType.BTC)
                .rate(new BigDecimal("40"))
                .build();

        CryptoStatModel model = CryptoStatModel.builder()
                .currency(CurrencyType.BTC)
                .oldest(oldest)
                .newest(newest)
                .min(min)
                .max(max)
                .normalize(new BigDecimal("2.0"))
                .build();

        CryptoStatView view = mapper.toView(model);

        assertNotNull(view);
        assertEquals(CurrencyTypeDto.BTC, view.getCurrency());

        assertNotNull(view.getOldest());
        assertEquals(1L, view.getOldest().getTs());
        assertEquals(CurrencyTypeDto.BTC, view.getOldest().getCurrency());
        assertEquals(new BigDecimal("10"), view.getOldest().getRate());

        assertNotNull(view.getNewest());
        assertEquals(3L, view.getNewest().getTs());

        assertNotNull(view.getMin());
        assertEquals(new BigDecimal("5"), view.getMin().getRate());

        assertNotNull(view.getMax());
        assertEquals(new BigDecimal("40"), view.getMax().getRate());
    }

    @Test
    void toView_shouldBuildAllSupportedCryptosView_inExpectedOrder() {
        CryptoStatModel btc = CryptoStatModel.builder()
                .currency(CurrencyType.BTC)
                .normalize(new BigDecimal("2.0"))
                .build();

        CryptoStatModel xrp = CryptoStatModel.builder()
                .currency(CurrencyType.XRP)
                .normalize(new BigDecimal("1.0"))
                .build();

        // The mapper iterates over model.getStats(), so we provide an explicitly ordered SortedSet.
        // This avoids coupling the mapper test to CryptoStatModel#compareTo implementation details.
        SortedSet<CryptoStatModel> stats = new TreeSet<>(
                Comparator.comparing(CryptoStatModel::getNormalize, Comparator.reverseOrder())
                        .thenComparing(s -> s.getCurrency().name())
        );
        stats.add(btc);
        stats.add(xrp);

        AllCryptosStatModel model = AllCryptosStatModel.builder()
                .stats(stats)
                .build();

        AllSupportedCryptosView view = mapper.toView(model);

        assertNotNull(view);
        assertEquals(of(CurrencyTypeDto.BTC, CurrencyTypeDto.XRP), view.getCurrencies());
    }

    @Configuration
    @ComponentScan(basePackages = "kz.che.xm.crypto.mapping")
    static class TestConfig {
    }
}
