package crypto.investments.importer.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A simple record (POJO) that represents an item
 * parsed from an import resource
 */
@Builder
public record CurrencyPrice(
    String symbol,
    BigDecimal price,
    LocalDateTime timestamp) {
}
