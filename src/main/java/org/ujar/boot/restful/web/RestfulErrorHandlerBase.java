package org.ujar.boot.restful.web;

import static org.ujar.boot.restful.web.RestfulErrorHandlerUtils.getJsonPointerField;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public class RestfulErrorHandlerBase {

  public ErrorResponse<UnknownErrorMeta> handle(Exception exception, String code) {
    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        UnknownErrorMeta.fromException(exception)
    )));
  }

  public ErrorResponse<InvalidRequestBodyMeta> handle(MethodArgumentNotValidException exception, String code) {
    final var errors = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> {
          final var jsonPointer = getJsonPointerField(error);
          return new Error<>(
              code,
              jsonPointer + " " + error.getDefaultMessage(),
              new InvalidRequestBodyMeta(jsonPointer, error.getRejectedValue()));
        })
        .toList();
    return new ErrorResponse<>(errors);
  }

  public ErrorResponse<InvalidRequestBodyMeta> handle(HttpMessageNotReadableException exception, String code) {
    Error<InvalidRequestBodyMeta> error;
    if (exception.getCause() instanceof InvalidFormatException formatException) {
      error = getErrorForInvalidFormat(formatException, code);
    } else {
      error = new Error<>(
          code,
          exception.getMessage(),
          new InvalidRequestBodyMeta(null, null)
      );
    }
    return new ErrorResponse<>(List.of(error));
  }

  public ErrorResponse<InvalidRequestParameterMeta> handle(MissingServletRequestParameterException exception,
                                                           String code) {
    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        new InvalidRequestParameterMeta(exception.getParameterName(), null)
    )));
  }

  public ErrorResponse<InvalidRequestParameterMeta> handle(TypeMismatchException exception, String code) {
    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        new InvalidRequestParameterMeta(
            exception.getPropertyName(),
            Objects.requireNonNullElse(exception.getValue(), "").toString()
        ))));
  }

  public ErrorResponse<InvalidRequestParameterMeta> handle(MethodArgumentTypeMismatchException exception,
                                                           String code) {
    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        new InvalidRequestParameterMeta(
            exception.getName(),
            Objects.requireNonNullElse(exception.getValue(), "").toString()
        ))));
  }

  public ErrorResponse<InvalidRequestParameterMeta> handle(ConstraintViolationException exception, String code) {
    final var errors = exception.getConstraintViolations().stream()
        .map(constraintViolation -> {
              final var parameter = StreamSupport
                  .stream(constraintViolation.getPropertyPath().spliterator(), false)
                  .toList()
                  // Path has the following structure: Method name -> Parameter name.
                  // So we fetch 2nd element of the collection
                  .get(1)
                  .toString();
              return new Error<>(
                  code,
                  parameter + " " + constraintViolation.getMessage(),
                  new InvalidRequestParameterMeta(
                      parameter,
                      Objects.toString(constraintViolation.getInvalidValue(), null)
                  )
              );
            }
        )
        .toList();
    return new ErrorResponse<>(errors);
  }

  public ErrorResponse<InvalidHttpMethodMeta> handle(HttpRequestMethodNotSupportedException exception,
                                                     String code) {
    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        new InvalidHttpMethodMeta(
            exception.getMethod(),
            List.of(Objects.requireNonNullElseGet(exception.getSupportedMethods(), () -> new String[0]))
        )
    )));
  }

  public ErrorResponse<InvalidContentTypeMeta> handle(HttpMediaTypeNotSupportedException exception,
                                                      WebRequest webRequest, String code) {

    return new ErrorResponse<>(List.of(new Error<>(
        code,
        exception.getMessage(),
        new InvalidContentTypeMeta(
            webRequest.getHeader(HttpHeaders.CONTENT_TYPE),
            exception.getSupportedMediaTypes()
                .stream()
                .map(MimeType::toString)
                .toList()
        )
    ))
    );
  }

  protected Error<InvalidRequestBodyMeta> getErrorForInvalidFormat(InvalidFormatException formatException,
                                                                   String code) {
    final var jsonPointer = getJsonPointerField(formatException);
    String detail;
    if (formatException.getTargetType().isEnum()) {
      final var allowedValues = Stream.of(formatException.getTargetType().getEnumConstants())
          .map(Object::toString)
          .collect(Collectors.joining(","));

      detail = String.format("%s must be one of [%s]",
          jsonPointer,
          allowedValues);
    } else {
      detail = formatException.getOriginalMessage();
    }
    return new Error<>(
        code,
        detail,
        new InvalidRequestBodyMeta(
            jsonPointer,
            formatException.getValue()
        )
    );
  }


}
