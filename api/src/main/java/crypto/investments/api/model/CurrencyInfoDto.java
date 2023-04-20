package crypto.investments.api.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CurrencyInfoDto(
    String symbol,
    BigDecimal oldestPrice,
    BigDecimal newestPrice,
    BigDecimal minPrice,
    BigDecimal maxPrice) {
}
