package crypto.investments.api.exception;


/**
 * An unchecked exception that might be thrown when requested currency
 * is not found by the specified symbol
 */
public class CurrencyDoesntExistException extends RuntimeException {

    public CurrencyDoesntExistException(String symbol) {
        super("Currency '%s' doesn't exists".formatted(symbol));
    }
}
