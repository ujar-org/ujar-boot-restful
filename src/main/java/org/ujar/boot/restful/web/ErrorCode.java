package org.ujar.boot.restful.web;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCode {
  // 1xx represents validation errors
  public static final String REQUEST_BODY_INVALID = "101";
  public static final String REQUEST_PARAMETER_INVALID = "102";
  // 2xx represents bad web requests errors
  public static final String INVALID_HTTP_METHOD = "201";
  public static final String INVALID_REQUEST_CONTENT_TYPE = "202";
  // 3xx represents internal server error
  public static final String UNKNOWN_ERROR = "301";

}
