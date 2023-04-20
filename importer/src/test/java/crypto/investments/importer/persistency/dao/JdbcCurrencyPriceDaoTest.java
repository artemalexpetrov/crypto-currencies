package crypto.investments.importer.persistency.dao;

import crypto.investments.importer.model.CurrencyPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcCurrencyPriceDaoTest {

    private static final int EXPECTED_BATCH_SIZE = 10000;

    @Mock
    private JdbcTemplate jdbcTemplateMock;

    @InjectMocks
    private JdbcCurrencyPriceDao currencyPriceDao;

    @Captor
    private ArgumentCaptor<Collection<CurrencyPrice>> pricesCaptor;

    @Test
    void testInsertBatch() {
        // given
        CurrencyPrice price1 = getCurrencyPrice();
        CurrencyPrice price2 = getCurrencyPrice();
        List<CurrencyPrice> prices = List.of(price1, price2);

        // when
        currencyPriceDao.insertBatch(prices);

        // then
        verify(jdbcTemplateMock, times(1)).batchUpdate(anyString(), pricesCaptor.capture(), eq(EXPECTED_BATCH_SIZE), any());
        verifyNoMoreInteractions(jdbcTemplateMock);
        assertThat(pricesCaptor.getValue()).containsAll(prices);
    }

    @Test
    void testInsertBatch_whenNullIsGiven_expectException() {
        assertThrows(IllegalArgumentException.class, () -> currencyPriceDao.insertBatch(null));
    }

    private CurrencyPrice getCurrencyPrice() {
        return CurrencyPrice.builder().build();
    }
}