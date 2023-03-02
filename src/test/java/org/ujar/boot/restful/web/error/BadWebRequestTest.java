package org.ujar.boot.restful.web.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
class BadWebRequestTest extends DefaultRestfulErrorHandlerTestBase {

  @Autowired
  public BadWebRequestTest(MockMvc mockMvc,
                           ObjectMapper objectMapper) {
    super(mockMvc, objectMapper);
  }

  @Test
  @SneakyThrows
  void methodNotSupported() {
    mockMvc.perform(
            post("/request-param")
                .param("stringParam", "foo")
                .param("integerParam", "42")
        )
        .andDo(print())
        .andExpect(status().isMethodNotAllowed())
        .andExpect(content().json(
            """
                {
                  "errors": [
                    {
                      "code": "201",
                      "detail": "Request method 'POST' is not supported",
                      "meta": {
                        "invalidMethod": "POST",
                        "allowedMethods": ["GET"]
                      }
                    }
                  ]
                }"""
        ));
  }

  @Test
  @SneakyThrows
  void mediaTypeNotSupported() {
    mockMvc.perform(
            post("/request-body")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(createValidRequest()))
        )
        .andDo(print())
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(content().json(
            """
                {
                  "errors": [
                    {
                      "code": "202",
                      "detail": "Content-Type 'application/xml' is not supported",
                      "meta": {
                        "invalidType":"application/xml;charset=UTF-8",
                        "supportedTypes":["application/json"]
                      }
                    }
                  ]
                }"""
        ));
  }

  @Test
  @SneakyThrows
  void unknownException() {
    mockMvc.perform(
            get("/exception")
                .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(content().json(
            """
                {
                  "errors": [
                    {
                      "code": "301",
                      "detail": "Something bad happened...",
                      "meta": {
                        "exceptionClass": "IllegalStateException"
                      }
                    }
                  ]
                }"""
        ));
  }
}
