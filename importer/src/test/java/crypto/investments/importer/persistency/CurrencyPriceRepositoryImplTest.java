package crypto.investments.importer.persistency;

import crypto.investments.importer.model.CurrencyPrice;
import crypto.investments.importer.persistency.dao.CurrencyPriceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CurrencyPriceRepositoryImplTest {

    @Mock
    private CurrencyPriceDao priceDaoMock;

    @InjectMocks
    private CurrencyPriceRepositoryImpl priceRepository;

    @Test
    void testSaveAll() {
        // given
        CurrencyPrice price = CurrencyPrice.builder().build();
        List<CurrencyPrice> prices = List.of(price);

        // when
        priceRepository.saveAll(prices);

        // then
        verify(priceDaoMock, times(1)).insertBatch(prices);
        verifyNoMoreInteractions(priceDaoMock);
    }

    @Test
    void testSaveAll_givenNullArgument_expectException() {
        assertThrows(IllegalArgumentException.class, () -> priceRepository.saveAll(null));
    }
}