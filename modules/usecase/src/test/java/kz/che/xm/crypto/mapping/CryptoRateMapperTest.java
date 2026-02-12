package kz.che.xm.crypto.mapping;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.view.CryptoRateView;
import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.model.CryptoRateModel;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = CryptoRateMapperTest.TestConfig.class)
class CryptoRateMapperTest {

    @jakarta.annotation.Resource
    private CryptoRateMapper mapper;

    @Test
    void toModel_shouldMapEntity() {
        CryptoRateEntity entity = new CryptoRateEntity();
        entity.setId(UUID.randomUUID());
        entity.setTs(123L);
        entity.setCurrency(CurrencyType.BTC);
        entity.setRate(new BigDecimal("100.25"));

        CryptoRateModel model = mapper.toModel(entity);

        assertNotNull(model);
        assertEquals(123L, model.getTs());
        assertEquals(CurrencyType.BTC, model.getCurrency());
        assertEquals(new BigDecimal("100.25"), model.getRate());
    }

    @Test
    void toEntity_shouldIgnoreId() {
        CryptoRateModel model = CryptoRateModel.builder()
                .ts(777L)
                .currency(CurrencyType.ETH)
                .rate(new BigDecimal("42.0"))
                .build();

        CryptoRateEntity entity = mapper.toEntity(model);

        assertNotNull(entity);
        assertEquals(777L, entity.getTs());
        assertEquals(CurrencyType.ETH, entity.getCurrency());
        assertEquals(new BigDecimal("42.0"), entity.getRate());
        assertNull(entity.getId(), "id must be ignored by mapper");
    }

    @Test
    void toView_shouldMapModel() {
        CryptoRateModel model = CryptoRateModel.builder()
                .ts(999L)
                .currency(CurrencyType.XRP)
                .rate(new BigDecimal("1.5"))
                .build();

        CryptoRateView view = mapper.toView(model);

        assertNotNull(view);
        assertEquals(999L, view.getTs());
        assertEquals(CurrencyTypeDto.XRP, view.getCurrency());
        assertEquals(new BigDecimal("1.5"), view.getRate());
    }

    @Test
    void listMappings_shouldWork() {
        CryptoRateEntity e1 = new CryptoRateEntity();
        e1.setTs(1L);
        e1.setCurrency(CurrencyType.BTC);
        e1.setRate(new BigDecimal("10"));

        CryptoRateEntity e2 = new CryptoRateEntity();
        e2.setTs(2L);
        e2.setCurrency(CurrencyType.ETH);
        e2.setRate(new BigDecimal("20"));

        List<CryptoRateModel> models = mapper.toModel(List.of(e1, e2));
        assertEquals(2, models.size());

        List<CryptoRateEntity> entities = mapper.toEntity(models);
        assertEquals(2, entities.size());

        List<CryptoRateView> views = mapper.toView(models);
        assertEquals(2, views.size());
    }

    @Configuration
    @ComponentScan(basePackages = "kz.che.xm.crypto.mapping")
    static class TestConfig {
    }
}
