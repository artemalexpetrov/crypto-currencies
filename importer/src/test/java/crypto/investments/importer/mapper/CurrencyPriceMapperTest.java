package crypto.investments.importer.mapper;

import crypto.investments.importer.model.CurrencyPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CurrencyPriceMapperTest {

    @InjectMocks
    private CurrencyPriceMapper mapper;

    @Test
    void testMapFieldSet() {
        // given
        LocalDateTime timestamp = LocalDateTime.now();
        long timestampMilliseconds = timestamp.toInstant(ZoneOffset.UTC).toEpochMilli();
        String symbol = "BTC";
        String price = "150.1";

        String[] tokens = new String[]{String.valueOf(timestampMilliseconds), symbol, price};
        FieldSet fieldSet = new DefaultFieldSet(tokens);

        // when
        CurrencyPrice currencyPrice = mapper.mapFieldSet(fieldSet);

        // then
        assertEquals(symbol, currencyPrice.symbol());
        assertEquals(new BigDecimal(price), currencyPrice.price());
        assertThat(currencyPrice.timestamp())
            .isCloseTo(timestamp, within(1, ChronoUnit.MILLIS));
    }
}