package org.ujar.boot.restful.web.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
class BadRequestParamTest extends DefaultRestfulErrorHandlerTestBase {

  @Autowired
  public BadRequestParamTest(MockMvc mockMvc,
                             ObjectMapper objectMapper) {
    super(mockMvc, objectMapper);
  }

  @Test
  @SneakyThrows
  void endpointIsAvailable() {
    mockMvc.perform(
            get("/request-param")
                .param("stringParam", "foo")
                .param("integerParam", "42")
        )
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  @SneakyThrows
  void requiredParamIsNotSet() {
    doRequestAndVerifyBody(
        "foo", null,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "102",
                  "detail": "Required request parameter 'integerParam' for method parameter type int is not present",
                  "meta": {
                    "parameter": "integerParam",
                    "invalidValue": null
                  }
                }
              ]
            }"""
    );
  }

  @Test
  @SneakyThrows
  void stringSizeConstraint() {
    doRequestAndVerifyBody(
        "zzzzzzzzzzzzz", "5",
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "102",
                  "detail": "stringParam size must be between 0 and 10",
                  "meta": {
                    "parameter": "stringParam",
                    "invalidValue": "zzzzzzzzzzzzz"
                  }
                }
              ]
            }"""
    );
  }

  @ParameterizedTest
  @CsvSource(value = {
      "-15,intParam must be greater than or equal to 0",
      "10000,intParam must be less than or equal to 1000"
  })
  void maxAndMinNumber(String number, String errorMessage) {
    doRequestAndVerifyBody(
        "foo", number,
        //language=JSON
        "{\n"
        + "  \"errors\": [\n"
        + "    {\n"
        + "      \"code\": \"102\",\n"
        + "      \"detail\": \"" + errorMessage + "\",\n"
        + "      \"meta\": {\n"
        + "        \"parameter\": \"intParam\",\n"
        + "        \"invalidValue\": \"" + number + "\"\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}"
    );
  }

  @Test
  @SneakyThrows
  void nonNumeric() {
    doRequestAndVerifyBody(
        "foo", "abc",
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "102",
                  "detail": "Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \\"abc\\"",
                  "meta": {
                    "parameter": "integerParam",
                    "invalidValue": "abc"
                  }
                }
              ]
            }"""
    );
  }

  @SneakyThrows
  void doRequestAndVerifyBody(String stringParam, String intParam, String body) {
    mockMvc.perform(
            get("/request-param")
                .param("stringParam", stringParam)
                .param("integerParam", intParam)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().json(body));
  }
}
