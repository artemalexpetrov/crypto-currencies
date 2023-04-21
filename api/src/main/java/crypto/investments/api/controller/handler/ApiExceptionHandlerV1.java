package crypto.investments.api.controller.handler;

import crypto.investments.api.exception.CurrencyDoesntExistException;
import crypto.investments.api.exception.InsufficientCurrencyDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for APIv1
 */
@RestControllerAdvice
public class ApiExceptionHandlerV1 {

    @ExceptionHandler(CurrencyDoesntExistException.class)
    public ProblemDetail handleCurrencyDoesntExistsException(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InsufficientCurrencyDataException.class)
    public ProblemDetail handleInsufficientDataException(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

}
