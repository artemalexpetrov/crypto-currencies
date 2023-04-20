package crypto.investments.api.persistency;

import crypto.investments.api.persistency.dao.CurrencyInfoDao;
import crypto.investments.api.persistency.model.CurrencyInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CurrencyInfoRepositoryImplTest {

    @Mock
    private CurrencyInfoDao currencyInfoDaoMock;

    @InjectMocks
    private CurrencyInfoRepositoryImpl repository;

    @Test
    void testCurrencyExists() {
        // given
        String btcSymbol = "BTC";
        String ltcSymbol = "LTC";

        when(currencyInfoDaoMock.currencyExists(btcSymbol)).thenReturn(true);
        when(currencyInfoDaoMock.currencyExists(ltcSymbol)).thenReturn(false);

        // when / then
        assertTrue(repository.currencyExists(btcSymbol));
        assertFalse(repository.currencyExists(ltcSymbol));

        verify(currencyInfoDaoMock, Mockito.times(1)).currencyExists(btcSymbol);
        verify(currencyInfoDaoMock, Mockito.times(1)).currencyExists(ltcSymbol);
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetCurrencyInfo_whenCurrencyExist_expectNonEmptyOptional() {
        // given
        String btcSymbol = "BTC";
        CurrencyInfo currencyInfo = CurrencyInfo.builder().build();
        when(currencyInfoDaoMock.getCurrencyInfo(btcSymbol)).thenReturn(currencyInfo);

        // when
        Optional<CurrencyInfo> btcInfo = repository.getCurrencyInfo(btcSymbol);

        // then
        assertThat(btcInfo)
            .isPresent()
            .contains(currencyInfo);

        verify(currencyInfoDaoMock, times(1)).getCurrencyInfo(btcSymbol);
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetCurrencyInfo_whenCurrencySymbolIsNull_expectIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.getCurrencyInfo(null));
        verifyNoInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetCurrencyInfo_whenCurrencyDoesntExist_expectEmptyOptional() {
        // given
        String btcSymbol = "BTC";
        when(currencyInfoDaoMock.getCurrencyInfo(any())).thenReturn(null);

        // when
        Optional<CurrencyInfo> btcInfo = repository.getCurrencyInfo(btcSymbol);

        // then
        assertThat(btcInfo).isEmpty();
        verify(currencyInfoDaoMock, times(1)).getCurrencyInfo(btcSymbol);
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetAllCurrencyInfo() {
        // given
        CurrencyInfo btcInfo = CurrencyInfo.builder().build();
        CurrencyInfo ltcInfo = CurrencyInfo.builder().build();
        List<CurrencyInfo> currencies = List.of(btcInfo, ltcInfo);
        when(currencyInfoDaoMock.getAllCurrencyInfo()).thenReturn(currencies);

        // when
        List<CurrencyInfo> retrievedInfo = repository.getAllCurrencyInfo();

        // then
        assertThat(retrievedInfo)
            .hasSize(currencies.size())
            .containsAll(currencies);

        verify(currencyInfoDaoMock, times(1)).getAllCurrencyInfo();
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetAllCurrencyInfo_whenDataIsEmpty_expectEmptyList() {
        // given
        when(currencyInfoDaoMock.getAllCurrencyInfo()).thenReturn(List.of());

        // when
        List<CurrencyInfo> retrievedInfo = repository.getAllCurrencyInfo();

        // then
        assertThat(retrievedInfo).isEmpty();
        verify(currencyInfoDaoMock, times(1)).getAllCurrencyInfo();
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetBestCurrencyForDate() {
        // given
        LocalDate date = LocalDate.now();
        CurrencyInfo currencyInfo = CurrencyInfo.builder().build();
        when(currencyInfoDaoMock.getBestCurrencyForDate(any())).thenReturn(currencyInfo);

        // when
        Optional<CurrencyInfo> bestCurrencyInfo = repository.getBestCurrencyForDate(date);

        // then
        assertThat(bestCurrencyInfo)
            .isPresent()
            .contains(currencyInfo);

        verify(currencyInfoDaoMock, times(1)).getBestCurrencyForDate(date);
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetBestCurrencyForDate_whenDataIsEmpty_expectEmptyOptional() {
        // given
        LocalDate date = LocalDate.now();
        when(currencyInfoDaoMock.getBestCurrencyForDate(any())).thenReturn(null);

        // when
        Optional<CurrencyInfo> bestCurrencyInfo = repository.getBestCurrencyForDate(date);

        // then
        assertThat(bestCurrencyInfo).isEmpty();
        verify(currencyInfoDaoMock, times(1)).getBestCurrencyForDate(date);
        verifyNoMoreInteractions(currencyInfoDaoMock);
    }

    @Test
    void testGetBestCurrencyForDate_whenNullableDateGiven_expectIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.getBestCurrencyForDate(null));
        verifyNoInteractions(currencyInfoDaoMock);
    }
}