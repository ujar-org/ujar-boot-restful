package org.ujar.boot.restful.web;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.ujar.boot.restful.web.dto.ErrorCode;
import org.ujar.boot.restful.web.dto.ErrorResponse;
import org.ujar.boot.restful.web.dto.InvalidContentTypeMeta;
import org.ujar.boot.restful.web.dto.InvalidHttpMethodMeta;
import org.ujar.boot.restful.web.dto.InvalidRequestBodyMeta;
import org.ujar.boot.restful.web.dto.InvalidRequestParameterMeta;
import org.ujar.boot.restful.web.dto.UnknownErrorMeta;

@Slf4j
@RestControllerAdvice
@ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
public class DefaultRestfulErrorHandler extends RestfulErrorHandlerBase {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse<UnknownErrorMeta> handleCommonExceptionHandler(Exception exception) {
    log.error("Exception during call", exception);
    return this.handle(exception, ErrorCode.UNKNOWN_ERROR);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestBodyMeta> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception) {
    log.info("Method argument is not valid", exception);
    return this.handle(exception, ErrorCode.REQUEST_BODY_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestBodyMeta> handleHttpMessageNotReadable(
      @NotNull HttpMessageNotReadableException exception) {
    log.info("HTTP message is not readable", exception);
    return this.handle(exception, ErrorCode.REQUEST_BODY_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestParameterMeta> handleMissingServletRequestParameter(
      MissingServletRequestParameterException exception) {
    log.info("Missing request parameter", exception);
    return this.handle(exception, ErrorCode.REQUEST_PARAMETER_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestParameterMeta> handleTypeMismatch(
      TypeMismatchException exception) {
    log.info("Type mismatch exception", exception);
    return this.handle(exception, ErrorCode.REQUEST_PARAMETER_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestParameterMeta> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException exception) {
    log.info("Method argument type mismatch exception", exception);
    return this.handle(exception, ErrorCode.REQUEST_PARAMETER_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse<InvalidRequestParameterMeta> handleConstraintViolationException(
      ConstraintViolationException exception) {
    log.info("Constraint violation", exception);
    return this.handle(exception, ErrorCode.REQUEST_PARAMETER_INVALID);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorResponse<InvalidHttpMethodMeta> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException exception) {
    log.info("Method not supported", exception);
    return this.handle(exception, ErrorCode.INVALID_HTTP_METHOD);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public ErrorResponse<InvalidContentTypeMeta> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException exception,
      WebRequest webRequest) {
    log.info("Media type not supported", exception);
    return this.handle(exception, webRequest, ErrorCode.INVALID_REQUEST_CONTENT_TYPE);
  }

}
