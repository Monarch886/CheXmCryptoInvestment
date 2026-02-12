package kz.che.xm.crypto.exception;

import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import kz.che.xm.crypto.dto.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kz.che.xm.crypto.dto.exception.CryptoInvestmentError.REQUIRED_PARAM_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatusCode.valueOf;


@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {
    private static final String UNEXPECTED_EXCEPTION = "UNEXPECTED_EXCEPTION";

    @ExceptionHandler({CryptoInvestmentException.class})
    public ResponseEntity<ErrorResponse> handleServiceExceptions(CryptoInvestmentException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(exception.getError().name())
                .description(exception.getDescription())
                .build();
        log.error(exception.getDescription(), exception);
        return new ResponseEntity<>(errorResponse, valueOf(exception.getError().getStatus()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleAny(Exception exception) {
        log.error(exception.getMessage(), exception);
        ErrorResponse response = ErrorResponse.builder()
                .error(UNEXPECTED_EXCEPTION)
                .description(exception.getMessage())
                .build();
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleReqParam(Exception exception) {
        log.error(exception.getMessage(), exception);
        ErrorResponse response = ErrorResponse.builder()
                .error(REQUIRED_PARAM_NOT_FOUND.name())
                .description(exception.getMessage())
                .build();
        return new ResponseEntity<>(response, BAD_REQUEST);
    }
}

