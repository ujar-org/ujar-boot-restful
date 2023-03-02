package org.ujar.boot.restful.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class TestController {

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping(value = "/request-body", consumes = MediaType.APPLICATION_JSON_VALUE)
  void testRequestBody(@Valid @RequestBody @NotNull final RequestDto request) {
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @GetMapping(value = "/request-param")
  void testRequestParam(
      @RequestParam @Size(max = 10) final String stringParam,
      @RequestParam(name = "integerParam") @Max(1000) @Min(0) final int intParam
  ) {
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/path-variable/{number}/{enum}")
  void testPathVariable(
      @PathVariable(name = "number") final int numberVariable,
      @PathVariable(name = "enum") final StatusType enumPathVariable
  ) {
  }

  @PostMapping(
      value = "/produce-type",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  void testProducesType() {
  }

  @GetMapping("/exception")
  void testThrowException() {
    throw new IllegalStateException("Something bad happened...");
  }

}
