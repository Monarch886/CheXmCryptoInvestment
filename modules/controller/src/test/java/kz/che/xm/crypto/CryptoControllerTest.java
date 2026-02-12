package kz.che.xm.crypto;

import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.dto.view.AllSupportedCryptosView;
import kz.che.xm.crypto.dto.view.CryptoRateView;
import kz.che.xm.crypto.dto.view.CryptoStatView;
import kz.che.xm.crypto.mapping.CryptoStatMapper;
import kz.che.xm.crypto.mapping.CurrencyTypeMapper;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoRateModel;
import kz.che.xm.crypto.model.CryptoStatModel;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.TreeSet;

import static java.util.List.of;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CryptoController.class)
@ContextConfiguration(classes = CryptoController.class)
public class CryptoControllerTest {

    @MockitoBean
    private CryptoUsecase usecase;
    @MockitoBean
    private CryptoStatMapper statMapper;
    @MockitoBean
    private CurrencyTypeMapper currencyMapper;


    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllCurrencies() throws Exception {
        // given
        CryptoStatModel expectedFirst = CryptoStatModel.builder()
                .currency(CurrencyType.XRP)
                .normalize(new BigDecimal(1))
                .build();
        CryptoStatModel expectedSecond = CryptoStatModel.builder()
                .currency(CurrencyType.BTC)
                .normalize(new BigDecimal(2))
                .build();
        AllCryptosStatModel expectedModel = new AllCryptosStatModel(new TreeSet<>(of(expectedFirst, expectedSecond)));
        AllSupportedCryptosView expectedView = new AllSupportedCryptosView(of(CurrencyTypeDto.BTC, CurrencyTypeDto.XRP));

        when(usecase.getCurrenciesCacheb(new CryptoSearchRequest())).thenReturn(expectedModel);
        when(statMapper.toView(expectedModel)).thenReturn(expectedView);
        when(currencyMapper.toDto(CurrencyType.XRP)).thenReturn(CurrencyTypeDto.XRP);

        //when-then
        mockMvc.perform(get("/api/v1/currencies")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencies.[0]").value(equalTo("BTC")))
                .andExpect(jsonPath("$.currencies.[1]").value(equalTo("XRP")))
        ;
    }

    @Test
    void getCryptoStat() throws Exception {
        // given
        CryptoRateView expectedOldest = CryptoRateView.builder()
                .ts(1230000)
                .currency(CurrencyTypeDto.BTC)
                .rate(BigDecimal.valueOf(10000))
                .build();
        CryptoRateView expectedNewest = CryptoRateView.builder()
                .ts(1000000)
                .currency(CurrencyTypeDto.BTC)
                .rate(BigDecimal.valueOf(10000))
                .build();
        CryptoRateView expectedMax = CryptoRateView.builder()
                .ts(1120000)
                .currency(CurrencyTypeDto.BTC)
                .rate(BigDecimal.valueOf(20000))
                .build();
        CryptoRateView expectedMin = CryptoRateView.builder()
                .ts(1110000)
                .currency(CurrencyTypeDto.BTC)
                .rate(BigDecimal.valueOf(1000))
                .build();
        CryptoStatView expected = CryptoStatView.builder()
                .oldest(expectedOldest)
                .newest(expectedNewest)
                .max(expectedMax)
                .min(expectedMin)
                .build();

        CryptoRateModel expectedOldestModel = CryptoRateModel.builder()
                .ts(1230000)
                .currency(CurrencyType.BTC)
                .rate(BigDecimal.valueOf(10000))
                .build();
        CryptoRateModel expectedNewestModel = CryptoRateModel.builder()
                .ts(1000000)
                .currency(CurrencyType.BTC)
                .rate(BigDecimal.valueOf(10000))
                .build();
        CryptoRateModel expectedMaxModel = CryptoRateModel.builder()
                .ts(1120000)
                .currency(CurrencyType.BTC)
                .rate(BigDecimal.valueOf(20000))
                .build();
        CryptoRateModel expectedMinModel = CryptoRateModel.builder()
                .ts(1110000)
                .currency(CurrencyType.BTC)
                .rate(BigDecimal.valueOf(1000))
                .build();
        CryptoStatModel expectedModel = CryptoStatModel.builder()
                .oldest(expectedOldestModel)
                .newest(expectedNewestModel)
                .max(expectedMaxModel)
                .min(expectedMinModel)
                .currency(CurrencyType.BTC)
                .normalize(BigDecimal.valueOf(19))
                .build();

        when(usecase.getStat(CryptoSearchRequest.builder()
                .currency(CurrencyTypeDto.BTC)
                .build())).thenReturn(expectedModel);
        when(statMapper.toView(expectedModel)).thenReturn(expected);
        //when-then
        mockMvc.perform(get("/api/v1/stat")
                        .contentType(APPLICATION_JSON)
                        .param("currency", "BTC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oldest.ts").value(1230000))
                .andExpect(jsonPath("$.oldest.currency").value(equalTo("BTC")))
                .andExpect(jsonPath("$.oldest.rate").value(BigDecimal.valueOf(10000)))
                .andExpect(jsonPath("$.newest.ts").value(1000000))
                .andExpect(jsonPath("$.newest.currency").value(equalTo("BTC")))
                .andExpect(jsonPath("$.newest.rate").value(BigDecimal.valueOf(10000)))
                .andExpect(jsonPath("$.max.ts").value(1120000))
                .andExpect(jsonPath("$.max.currency").value(equalTo("BTC")))
                .andExpect(jsonPath("$.max.rate").value(BigDecimal.valueOf(20000)))
                .andExpect(jsonPath("$.min.ts").value(1110000))
                .andExpect(jsonPath("$.min.currency").value(equalTo("BTC")))
                .andExpect(jsonPath("$.min.rate").value(BigDecimal.valueOf(1000)));
    }

    @Test
    void getTopCrypto() throws Exception {
        // given
        when(usecase.getTop(CryptoSearchRequest.builder()
                .dateFrom(ZonedDateTime.parse("2022-01-01T00:00:00+05:00"))
                .dateTo(ZonedDateTime.parse("2022-01-02T00:00:00+05:00"))
                .build())).thenReturn(CurrencyType.XRP.name());
        when(currencyMapper.toDto(CurrencyType.XRP.name())).thenReturn(CurrencyTypeDto.XRP);

        //when-then
        mockMvc.perform(get("/api/v1/top")
                        .contentType(APPLICATION_JSON)
                        .param("date", "2022-01-01T00:00:00+05:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(equalTo("XRP")));
    }
}
