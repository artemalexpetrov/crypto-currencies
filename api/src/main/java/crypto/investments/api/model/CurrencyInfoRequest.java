package crypto.investments.api.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * Simple record that represents a request
 * for info for the one currency
 */
@Validated
public record CurrencyInfoRequest(

    @NotBlank
    String symbol) {
}
