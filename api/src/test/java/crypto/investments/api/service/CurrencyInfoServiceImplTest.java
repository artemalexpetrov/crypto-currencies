package crypto.investments.api.service;

import crypto.investments.api.exception.CurrencyDoesntExistException;
import crypto.investments.api.exception.InsufficientCurrencyDataException;
import crypto.investments.api.mapper.CurrencyInfoMapper;
import crypto.investments.api.mapper.CurrencyInfoMapperImpl;
import crypto.investments.api.model.BestCurrencyInfoRequest;
import crypto.investments.api.model.CurrencyInfoDto;
import crypto.investments.api.model.CurrencyInfoRequest;
import crypto.investments.api.persistency.CurrencyInfoRepository;
import crypto.investments.api.persistency.model.CurrencyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyInfoServiceImplTest {

    @Mock
    private CurrencyInfoRepository repositoryMock;
    private CurrencyInfoMapper mapper;

    private CurrencyInfoService infoService;

    @BeforeEach
    void setUp() {
        mapper = spy(new CurrencyInfoMapperImpl());
        infoService = new CurrencyInfoServiceImpl(repositoryMock, mapper);
    }

    @Test
    void testGetCurrencyInfo() {
        // given
        String btcSymbol = "BTC";
        CurrencyInfo currencyInfo = createCurrencyInfoBuilder(btcSymbol).build();

        CurrencyInfoRequest request = new CurrencyInfoRequest(btcSymbol);
        when(repositoryMock.currencyExists(btcSymbol)).thenReturn(true);
        when(repositoryMock.getCurrencyInfo(btcSymbol)).thenReturn(Optional.of(currencyInfo));

        // when
        CurrencyInfoDto currencyInfoDto = infoService.getCurrencyInfo(request);

        // then
        assertCurrencyInfoDto(currencyInfo, currencyInfoDto);

        verify(repositoryMock, times(1)).currencyExists(btcSymbol);
        verify(repositoryMock, times(1)).getCurrencyInfo(btcSymbol);
        verify(mapper, times(1)).currencyInfoToDto(currencyInfo);

        verifyNoMoreInteractions(repositoryMock);
        verifyNoMoreInteractions(mapper);
    }


    @Test
    void testGetCurrencyInfo_whenCurrencyDoesntExist_expectException() {
        // given
        String btcSymbol = "BTC";
        CurrencyInfoRequest request = new CurrencyInfoRequest(btcSymbol);
        when(repositoryMock.currencyExists(btcSymbol)).thenReturn(false);

        // when / then
        assertThrows(CurrencyDoesntExistException.class, () -> infoService.getCurrencyInfo(request));

        verify(repositoryMock, times(1)).currencyExists(btcSymbol);
        verifyNoMoreInteractions(repositoryMock);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetCurrencyInfo_whenInsufficientData_expectException() {
        // given
        String btcSymbol = "BTC";
        CurrencyInfoRequest request = new CurrencyInfoRequest(btcSymbol);
        when(repositoryMock.currencyExists(btcSymbol)).thenReturn(true);
        when(repositoryMock.getCurrencyInfo(btcSymbol)).thenReturn(Optional.empty());

        // when / then
        assertThrows(InsufficientCurrencyDataException.class, () -> infoService.getCurrencyInfo(request));

        verify(repositoryMock, times(1)).currencyExists(btcSymbol);
        verify(repositoryMock, times(1)).getCurrencyInfo(btcSymbol);
        verifyNoMoreInteractions(repositoryMock);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetAllCurrencies() {
        // given
        CurrencyInfo btcInfo = createCurrencyInfoBuilder("BTC").build();
        CurrencyInfo ltcInfo = createCurrencyInfoBuilder("LTC").build();
        when(repositoryMock.getAllCurrencyInfo()).thenReturn(List.of(btcInfo, ltcInfo));

        // when
        List<CurrencyInfoDto> allCurrencies = infoService.getAllCurrencies();

        // then
        assertCurrencyInfoDto(btcInfo, allCurrencies.get(0));
        assertCurrencyInfoDto(ltcInfo, allCurrencies.get(1));

        verify(repositoryMock, times(1)).getAllCurrencyInfo();
        verify(mapper, times(1)).currenciesInfoToDto(anyCollection());
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void testGetAllCurrencies_whenDataIsEmpty_expectEmptyList() {
        // given
        when(repositoryMock.getAllCurrencyInfo()).thenReturn(List.of());

        // when
        List<CurrencyInfoDto> allCurrencies = infoService.getAllCurrencies();

        // then
        assertThat(allCurrencies).isEmpty();

        verify(repositoryMock, times(1)).getAllCurrencyInfo();
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void testGetBestCurrencyForDate() {
        // given
        LocalDate date = LocalDate.now();
        CurrencyInfo currencyInfo = createCurrencyInfoBuilder("BTC").build();
        BestCurrencyInfoRequest request = new BestCurrencyInfoRequest(date);

        when(repositoryMock.getBestCurrencyForDate(date)).thenReturn(Optional.of(currencyInfo));

        // when
        CurrencyInfoDto currencyInfoDto = infoService.getBestCurrencyForDate(request);

        // then
        assertCurrencyInfoDto(currencyInfo, currencyInfoDto);

        verify(repositoryMock, times(1)).getBestCurrencyForDate(date);
        verify(mapper, times(1)).currencyInfoToDto(currencyInfo);

        verifyNoMoreInteractions(repositoryMock);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void testGetBestCurrencyForDate_whenInsufficientData_expectException() {
        // given
        LocalDate date = LocalDate.now();
        BestCurrencyInfoRequest request = new BestCurrencyInfoRequest(date);
        when(repositoryMock.getBestCurrencyForDate(date)).thenReturn(Optional.empty());

        // when
        assertThrows(InsufficientCurrencyDataException.class, () -> infoService.getBestCurrencyForDate(request));

        // then
        verify(repositoryMock, times(1)).getBestCurrencyForDate(date);
        verifyNoMoreInteractions(repositoryMock);
        verifyNoInteractions(mapper);
    }

    private void assertCurrencyInfoDto(CurrencyInfo currencyInfo, CurrencyInfoDto currencyInfoDto) {
        assertEquals(currencyInfo.getSymbol(), currencyInfoDto.symbol());
        assertEquals(currencyInfo.getOldestPrice(), currencyInfoDto.oldestPrice());
        assertEquals(currencyInfo.getNewestPrice(), currencyInfoDto.newestPrice());
        assertEquals(currencyInfo.getMinPrice(), currencyInfoDto.minPrice());
        assertEquals(currencyInfo.getMaxPrice(), currencyInfoDto.maxPrice());
    }

    private CurrencyInfo.CurrencyInfoBuilder createCurrencyInfoBuilder(String symbol) {
        return CurrencyInfo.builder()
            .symbol(symbol)
            .oldestPrice(new BigDecimal("5.00"))
            .newestPrice(new BigDecimal("10.00"))
            .minPrice(new BigDecimal("1.00"))
            .maxPrice(new BigDecimal("20.00"));
    }
}