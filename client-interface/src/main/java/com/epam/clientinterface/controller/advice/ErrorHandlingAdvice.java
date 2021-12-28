package com.epam.clientinterface.controller.advice;

import com.epam.clientinterface.controller.dto.response.ErrorResponse;
import com.epam.clientinterface.domain.exception.AccountIsClosedException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForCard;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForExternalTransferException;
import com.epam.clientinterface.domain.exception.AccountIsNotSupposedForWithdraw;
import com.epam.clientinterface.domain.exception.AccountNotFoundException;
import com.epam.clientinterface.domain.exception.CardNotFoundException;
import com.epam.clientinterface.domain.exception.ChangePinException;
import com.epam.clientinterface.domain.exception.CurrencyNotFoundException;
import com.epam.clientinterface.domain.exception.IncorrectPinException;
import com.epam.clientinterface.domain.exception.NotEnoughMoneyException;
import com.epam.clientinterface.domain.exception.UserAlreadyExistException;
import com.epam.clientinterface.domain.exception.UserNotFoundException;
import com.epam.clientinterface.domain.exception.UsernameAlreadyTakenException;
import java.util.HashMap;
import javax.validation.ConstraintViolationException;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandlingAdvice extends ResponseEntityExceptionHandler {

    @Override
    protected @NonNull ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatus status,
        @NonNull WebRequest request
    ) {
        var body = new HashMap<>();
        body.put("type", "validation");
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("errors", ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(v -> {
                var fieldError = new HashMap<>();
                fieldError.put("field", v.getField());
                fieldError.put("type", v.getCode());
                fieldError.put("error", v.getDefaultMessage());

                return fieldError;
            })
            .toArray()
        );

        return handleExceptionInternal(ex, body, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @Override
    public @NonNull ResponseEntity<Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatus status,
        @NonNull WebRequest request
    ) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("badRequest", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @Override
    public @NonNull ResponseEntity<Object> handleMissingPathVariable(
        @NonNull MissingPathVariableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatus status,
        @NonNull WebRequest request
    ) {

        ErrorResponse body = new ErrorResponse("badPath", HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(Exception ex, WebRequest request) {
        ErrorResponse body = new ErrorResponse("invalidPath", HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFound(AccountNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("accountNotFound", HttpStatus.NOT_FOUND),
            new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<Object> handleNotEnoughMoney(NotEnoughMoneyException ex, WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("notEnoughMoney", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(CardNotFoundException.class)
    public final ResponseEntity<Object> handleCardNotFound(Exception ex, WebRequest request) {
        ErrorResponse body = new ErrorResponse("cardNotFound", HttpStatus.NOT_FOUND);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccountIsNotSupposedForExternalTransferException.class)
    public ResponseEntity<Object> handleAccountIsNotSupposedForExternalTransfer(
        AccountIsNotSupposedForExternalTransferException exception,
        WebRequest request
    ) {
        return handleExceptionInternal(
            exception,
            new ErrorResponse("accountIsNotSupposedForExternalTransfer", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistException ex,
                                                          WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("userAlreadyExists", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Object> handleUsernameAlreadyTaken(UsernameAlreadyTakenException ex,
                                                             WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("usernameAlreadyTaken", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("userNotFound", HttpStatus.NOT_FOUND),
            new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(AccountIsNotSupposedForWithdraw.class)
    public ResponseEntity<Object> handleAccountIsNotSupposedForWithdraw(AccountIsNotSupposedForWithdraw ex,
                                                                        WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("accountIsNotSupposedForWithdraw", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(AccountIsNotSupposedForCard.class)
    public ResponseEntity<Object> handleAccountIsNotSupposedForCard(AccountIsNotSupposedForCard ex,
                                                                    WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("accountIsNotSupposedForCard", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<Object> handleCurrencyNotFoundException(CurrencyNotFoundException ex,
                                                                  WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("currencyNotFound", HttpStatus.NOT_FOUND),
            new HttpHeaders(),
            HttpStatus.NOT_FOUND,
            request
        );
    }

    @ExceptionHandler(AccountIsClosedException.class)
    public ResponseEntity<Object> handleAccountIsClosedException(AccountIsClosedException ex, WebRequest request) {
        return handleExceptionInternal(
            ex,
            new ErrorResponse("accountIsClosed", HttpStatus.BAD_REQUEST),
            new HttpHeaders(),
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @ExceptionHandler(IncorrectPinException.class)
    public ResponseEntity<Object> handleChangePinException(IncorrectPinException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Pin code is not valid", HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ChangePinException.class)
    public ResponseEntity<Object> handleChangePinException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("Limit of attempts", HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
