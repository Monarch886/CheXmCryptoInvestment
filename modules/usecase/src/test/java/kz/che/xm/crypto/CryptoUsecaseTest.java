package kz.che.xm.crypto;

import kz.che.xm.crypto.adapter.CryptoRepoAdapter;
import kz.che.xm.crypto.common.Sorter;
import kz.che.xm.crypto.dto.enums.CurrencyTypeDto;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentError;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import kz.che.xm.crypto.dto.reqres.CryptoSearchRequest;
import kz.che.xm.crypto.enity.CryptoRateEntity;
import kz.che.xm.crypto.impl.CryptoUsecaseImpl;
import kz.che.xm.crypto.mapping.CryptoRateMapper;
import kz.che.xm.crypto.mapping.CryptoSearchMapper;
import kz.che.xm.crypto.model.AllCryptosStatModel;
import kz.che.xm.crypto.model.CryptoRateModel;
import kz.che.xm.crypto.model.CryptoStatModel;
import kz.che.xm.crypto.search.CryptoSearch;
import kz.che.xm.crypto.type.CurrencyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CryptoUsecaseTest {

    @Mock
    private CryptoRepoAdapter repoAdapter;
    @Mock
    private CryptoRateMapper mapper;
    @Mock
    private CryptoSearchMapper searchMapper;

    @InjectMocks
    private CryptoUsecaseImpl usecase;

    @Test
    void getStat_shouldBuildStat_oldestNewestMinMaxAndNormalize() {
        CryptoSearchRequest request = CryptoSearchRequest.builder()
                .currency(CurrencyTypeDto.BTC)
                .build();

        CryptoSearch daoSearch = CryptoSearch.builder()
                .currency(CurrencyType.BTC)
                .build();

        List<CryptoRateModel> rates = List.of(
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.BTC).rate(valueOf(15)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.BTC).rate(valueOf(10)).build(), // min
                CryptoRateModel.builder().ts(3L).currency(CurrencyType.BTC).rate(valueOf(20)).build()  // max + newest
        );

        when(searchMapper.toDao(request)).thenReturn(daoSearch);
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of(mock(CryptoRateEntity.class)));
        when(mapper.toModel(any(List.class))).thenReturn(rates);

        CryptoStatModel stat = usecase.getStat(request);

        assertNotNull(stat);

        assertEquals(CurrencyType.BTC, stat.getCurrency());

        assertEquals(1L, stat.getOldest().getTs());
        assertEquals(3L, stat.getNewest().getTs());

        assertEquals(valueOf(10), stat.getMin().getRate());
        assertEquals(valueOf(20), stat.getMax().getRate());

        // (max - min) / min = (20 - 10) / 10 = 1.0
        assertEquals(new BigDecimal("1.0000000000"), stat.getNormalize());

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verify(mapper).toModel(any(List.class));
        verifyNoMoreInteractions(searchMapper, repoAdapter, mapper);
    }

    @Test
    void getCurrencies_shouldGroupByCurrency_buildStats_andSortByNormalizeDesc() {
        CryptoSearchRequest request = CryptoSearchRequest.builder().build();

        CryptoSearch daoSearch = CryptoSearch.builder().build();

        // Important: list is expected already sorted by ts (and currency) because usecase takes getFirst/getLast.
        // BTC: min=10 max=20 normalize=1.0
        // ETH: min=10 max=11 normalize=0.1
        List<CryptoRateModel> rates = List.of(
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.BTC).rate(valueOf(10)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.BTC).rate(valueOf(20)).build(),
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.ETH).rate(valueOf(10)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.ETH).rate(valueOf(11)).build()
        );

        when(searchMapper.toDao(request)).thenReturn(daoSearch);
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of(mock(CryptoRateEntity.class)));
        when(mapper.toModel(any(List.class))).thenReturn(rates);

        AllCryptosStatModel stats = usecase.getCurrenciesCacheb(request);

        assertNotNull(stats);
        assertEquals(2, stats.getStats().size());

        CryptoStatModel top = stats.getStats().getFirst();
        assertEquals(CurrencyType.BTC, top.getCurrency()); // highest normalize should be first

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verify(mapper).toModel(any(List.class));
        verifyNoMoreInteractions(searchMapper, repoAdapter, mapper);
    }

    @Test
    void getTop_shouldReturnFirstCurrencyNameFromSortedStats() {
        CryptoSearchRequest request = CryptoSearchRequest.builder().build();

        CryptoSearch daoSearch = CryptoSearch.builder().build();

        List<CryptoRateModel> rates = List.of(
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.BTC).rate(valueOf(10)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.BTC).rate(valueOf(20)).build(),
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.ETH).rate(valueOf(10)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.ETH).rate(valueOf(11)).build()
        );

        when(searchMapper.toDao(request)).thenReturn(daoSearch);
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of(mock(CryptoRateEntity.class)));
        when(mapper.toModel(any(List.class))).thenReturn(rates);

        String top = usecase.getTop(request);

        assertEquals("BTC", top);

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verify(mapper).toModel(any(List.class));
        verifyNoMoreInteractions(searchMapper, repoAdapter, mapper);
    }

    @Test
    void getCurrencies_shouldPassSorterToRepoSearch_tsAndCurrencyAsc() {
        CryptoSearchRequest request = CryptoSearchRequest.builder().build();

        CryptoSearch daoSearch = CryptoSearch.builder().build();

        when(searchMapper.toDao(request)).thenReturn(daoSearch);
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of(mock(CryptoRateEntity.class)));
        when(mapper.toModel(any(List.class))).thenReturn(List.of(
                CryptoRateModel.builder().ts(1L).currency(CurrencyType.BTC).rate(valueOf(10)).build(),
                CryptoRateModel.builder().ts(2L).currency(CurrencyType.BTC).rate(valueOf(20)).build()
        ));

        usecase.getCurrenciesCacheb(request);

        ArgumentCaptor<CryptoSearch> captor = ArgumentCaptor.forClass(CryptoSearch.class);
        verify(repoAdapter).getAllBySearch(captor.capture());

        CryptoSearch passed = captor.getValue();
        assertNotNull(passed);

        Sorter sorter = passed.getSort();
        assertNotNull(sorter);
        assertEquals(Sorter.DirectionType.ASC, sorter.getDirection());
        assertEquals(List.of("ts", "currency"), sorter.getProperty());
    }

    @Test
    void getStat_shouldThrowNotFound_whenRepoReturnsEmpty() {
        CryptoSearchRequest request = CryptoSearchRequest.builder()
                .currency(CurrencyTypeDto.BTC)
                .build();

        when(searchMapper.toDao(request)).thenReturn(CryptoSearch.builder().build());
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of());

        CryptoInvestmentException ex = assertThrows(CryptoInvestmentException.class, () -> usecase.getStat(request));
        assertEquals(CryptoInvestmentError.NOT_FOUND, ex.getError());

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void getCurrencies_shouldThrowNotFound_whenRepoReturnsEmpty() {
        CryptoSearchRequest request = CryptoSearchRequest.builder().build();

        when(searchMapper.toDao(request)).thenReturn(CryptoSearch.builder().build());
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of());

        CryptoInvestmentException ex = assertThrows(CryptoInvestmentException.class, () -> usecase.getCurrenciesCacheb(request));
        assertEquals(CryptoInvestmentError.NOT_FOUND, ex.getError());

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verifyNoInteractions(mapper);
    }

    @Test
    void getTop_shouldThrowNotFound_whenRepoReturnsEmpty() {
        CryptoSearchRequest request = CryptoSearchRequest.builder().build();

        when(searchMapper.toDao(request)).thenReturn(CryptoSearch.builder().build());
        when(repoAdapter.getAllBySearch(any(CryptoSearch.class))).thenReturn(List.of());

        CryptoInvestmentException ex = assertThrows(CryptoInvestmentException.class, () -> usecase.getTop(request));
        assertEquals(CryptoInvestmentError.NOT_FOUND, ex.getError());

        verify(searchMapper).toDao(request);
        verify(repoAdapter).getAllBySearch(any(CryptoSearch.class));
        verifyNoInteractions(mapper);
    }
}
