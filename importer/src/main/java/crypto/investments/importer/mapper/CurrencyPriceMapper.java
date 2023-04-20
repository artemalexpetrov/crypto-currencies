package crypto.investments.importer.mapper;

import crypto.investments.importer.model.CurrencyPrice;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * An implementation of {@link FieldSetMapper} that
 * maps data obtained from {@link FieldSet} to
 * create {@link CurrencyPrice} during the import
 */
@Component
public class CurrencyPriceMapper implements FieldSetMapper<CurrencyPrice> {

    private static final int TIMESTAMP_COLUMN_INDEX = 0;
    private static final int SYMBOL_COLUMN_INDEX = 1;
    private static final int PRICE_COLUMN_INDEX = 2;

    @Override
    public @NonNull CurrencyPrice mapFieldSet(FieldSet fieldSet) {
        return CurrencyPrice.builder()
            .symbol(fieldSet.readString(SYMBOL_COLUMN_INDEX))
            .price(fieldSet.readBigDecimal(PRICE_COLUMN_INDEX))
            .timestamp(readTimestamp(fieldSet))
            .build();
    }

    private LocalDateTime readTimestamp(FieldSet fieldSet) {
        long timestamp = fieldSet.readLong(TIMESTAMP_COLUMN_INDEX);
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC.normalized());
    }
}
