package crypto.investments.api.persistency.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Builder
@EqualsAndHashCode
public class CurrencyInfo implements Serializable {

    public static final int NORMALIZED_RANGE_SCALE = 4;

    private final String symbol;
    private final BigDecimal oldestPrice;
    private final BigDecimal newestPrice;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final BigDecimal normalizedRange;

    public static class CurrencyInfoBuilder {
        private BigDecimal normalizedRange;

        public CurrencyInfoBuilder normalizedRange(BigDecimal normalizedRang) {
            this.normalizedRange = normalizedRang.setScale(NORMALIZED_RANGE_SCALE, RoundingMode.HALF_UP);
            return this;
        }
    }
}
