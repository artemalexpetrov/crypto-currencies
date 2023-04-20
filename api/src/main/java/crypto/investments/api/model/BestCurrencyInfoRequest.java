package crypto.investments.api.model;

import jakarta.validation.constraints.PastOrPresent;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

/**
 * Simple record that represents a request
 * for the best currency for the specified date
 */
@Validated
public record BestCurrencyInfoRequest(


    LocalDate date) {
}
