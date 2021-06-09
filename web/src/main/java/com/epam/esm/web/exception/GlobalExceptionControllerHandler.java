package com.epam.esm.web.exception;

import com.epam.esm.service.exception.DuplicateEntityException;
import com.epam.esm.service.exception.InvalidJwtException;
import com.epam.esm.service.exception.InvalidParametersException;
import com.epam.esm.service.exception.NoSuchEntityException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionControllerHandler {
    private static final List<String> AVAILABLE_LOCALES = Arrays.asList("en_US", "ru_RU");
    private static final Locale DEFAULT_LOCALE = new Locale("en", "US");

    private final ResourceBundleMessageSource bundleMessageSource;

    @Autowired
    public GlobalExceptionControllerHandler(ResourceBundleMessageSource bundleMessageSource) {
        this.bundleMessageSource = bundleMessageSource;
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidJwtException(Locale locale) {
        return buildErrorResponse(resolveResourceBundle("jwt.invalid", locale),
                40100, HttpStatus.UNAUTHORIZED);
    }

    public ExceptionResponse buildNoJwtResponseObject(Locale locale) {
        return new ExceptionResponse(resolveResourceBundle("jwt.not.exist", locale), 40101);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(Locale locale) {
        return buildErrorResponse(resolveResourceBundle("user.bad.creds", locale),
                40102, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(Locale locale) {
        return buildErrorResponse(resolveResourceBundle("access.denied", locale),
                40300, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateEntityException(
            DuplicateEntityException e, Locale locale) {
        return buildErrorResponse(resolveResourceBundle(e.getMessage(), locale),
                40901, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidEntityException(
            MethodArgumentNotValidException e, Locale locale) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = e.getMessage();
        if (fieldError != null) {
            message = fieldError.getDefaultMessage();
        }
        return buildErrorResponse(resolveResourceBundle(message, locale),
                40000, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidParametersException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidParametersException(InvalidParametersException e,
                                                                              Locale locale) {
        return buildErrorResponse(resolveResourceBundle(e.getMessage(), locale),
                40001, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException
            (MissingServletRequestParameterException e) {
        return buildErrorResponse(e.getMessage(), 40002, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleTypeMismatchException(TypeMismatchException e) {
        return buildErrorResponse(e.getMessage(), 40003, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleNotReadableBodyException(Locale locale) {
        return buildErrorResponse(resolveResourceBundle("request.body.missing", locale),
                40004, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUpdateFieldsException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidUpdateFieldsException(
            InvalidUpdateFieldsException e, Locale locale) {
        return buildErrorResponse(resolveResourceBundle(e.getMessage(), locale),
                40005, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse(e.getMessage(), 40500, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoFoundException(NoHandlerFoundException e) {
        return buildErrorResponse(e.getMessage(), 40400, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchEntityException.class)
    public ResponseEntity<ExceptionResponse> handleNoSuchEntityException(NoSuchEntityException e,
                                                                         Locale locale) {
        return buildErrorResponse(resolveResourceBundle(e.getMessage(), locale),
                40401, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponse> handleOtherExceptions(Exception e) {
        e.printStackTrace();
        return buildErrorResponse(e.getMessage(), 50000, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String resolveResourceBundle(String key, Locale locale) {
        if (!AVAILABLE_LOCALES.contains(locale.toString())) {
            locale = DEFAULT_LOCALE;
        }
        return bundleMessageSource.getMessage(key, null, locale);
    }

    private ResponseEntity<ExceptionResponse> buildErrorResponse(String message, int code,
                                                                 HttpStatus status) {
        ExceptionResponse response = new ExceptionResponse(message, code);
        return new ResponseEntity<>(response, status);
    }
}
