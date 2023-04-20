package crypto.investments.api.exception;

/**
 * An unchecked exception that might be thrown when there is no
 * enough data to get info for the requested currency
 */
public class InsufficientCurrencyDataException extends RuntimeException {

    public InsufficientCurrencyDataException() {
        super("Insufficient for the specified period");
    }

    public InsufficientCurrencyDataException(String symbol) {
        super("Insufficient data for currency %s for the specified period".formatted(symbol));
    }
}
