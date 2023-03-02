package org.ujar.boot.restful.web.error;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.ujar.boot.restful.web.RequestDto;

@ExtendWith(SpringExtension.class)
class BadRequestBodyTest extends DefaultRestfulErrorHandlerTestBase {
  @Autowired
  public BadRequestBodyTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    super(mockMvc, objectMapper);
  }

  @Test
  @SneakyThrows
  void endpointIsAvailable() {
    mockMvc.perform(
            post("/request-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidRequest()))
        )
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @ParameterizedTest
  @CsvSource(value = {"-15,/numberField must be greater than or equal to 10",
      "1000,/numberField must be less than or equal to 100"})
  void maxAndMinNumber(int number, String errorMessage) {
    final var request = createValidRequest();
    request.setNumberField(number);

    doRequestAndVerifyBody(
        request,
        //language=JSON
        "{\n"
        + "  \"errors\": [\n"
        + "    {\n"
        + "      \"code\": \"101\",\n"
        + "      \"detail\": \"" + errorMessage + "\",\n"
        + "      \"meta\": {\n"
        + "        \"field\": \"/numberField\",\n"
        + "        \"invalidValue\": " + number + "\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}"
    );
  }

  @Test
  @SneakyThrows
  void numberNonNumeric() {
    RequestDto request = createValidRequest();
    ObjectNode node = objectMapper.valueToTree(request);
    node.remove("numberField");
    node.put("numberField", "not a number");

    doRequestAndVerifyBody(
        node,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "Cannot deserialize value of type `int` from String \\"not a number\\": not a valid `int` value",
                  "meta": {
                    "field": "/numberField",
                    "invalidValue": "not a number"
                  }
                }
              ]
            }"""
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"1,2,3,4", "1"})
  void listSize(String values) {
    final var request = createValidRequest();
    request.setStringListField(List.of(values.split(",")));

    final var invalidValue = request.getStringListField()
        .stream()
        .map(str -> "\"" + str + "\"")
        .collect(Collectors.joining(","));
    doRequestAndVerifyBody(
        request,
        //language=JSON
        "{\n"
        + "  \"errors\": [\n"
        + "    {\n"
        + "      \"code\": \"101\",\n"
        + "      \"detail\": \"/stringListField size must be between 2 and 3\",\n"
        + "      \"meta\": {\n"
        + "        \"field\": \"/stringListField\",\n"
        + "        \"invalidValue\": [" + invalidValue + "]\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}");
  }

  @Test
  void nonNullListElement() {
    final var request = createValidRequest();
    request.setStringListField(Arrays.asList("1", null, "2"));

    doRequestAndVerifyBody(
        request,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/stringListField/1 must not be null",
                  "meta": {
                    "field": "/stringListField/1",
                    "invalidValue": null
                  }
                }
              ]
            }""");
  }

  @Test
  void nestedObjectInvalidValue() {
    final var request = createValidRequest();
    request.getNestedObjectField().setStringField("");

    doRequestAndVerifyBody(
        request,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/nestedObjectField/stringField must not be blank",
                  "meta": {
                    "field": "/nestedObjectField/stringField",
                    "invalidValue": ""
                  }
                }
              ]
            }""");
  }

  @Test
  void nestedListInvalidValue() {
    final var request = createValidRequest();
    request.getNestedObjectListField().get(1).setStringField("");

    doRequestAndVerifyBody(
        request,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/nestedObjectListField/1/stringField must not be blank",
                  "meta": {
                    "field": "/nestedObjectListField/1/stringField",
                    "invalidValue": ""
                  }
                }
              ]
            }""");
  }

  @Test
  void stringNotBlank() {
    final var request = createValidRequest();
    request.setStringField("");

    doRequestAndVerifyBody(
        request,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/stringField must not be blank",
                  "meta": {
                    "field": "/stringField",
                    "invalidValue": ""
                  }
                }
              ]
            }""");
  }

  @Test
  void uuidInvalid() {
    RequestDto request = createValidRequest();
    ObjectNode node = objectMapper.valueToTree(request);
    node.remove("uuidField");
    node.put("uuidField", "not a uuid");

    doRequestAndVerifyBody(
        node,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "Cannot deserialize value of type `java.util.UUID` from String \\"not a uuid\\": UUID has to be represented by standard 36-char representation",
                  "meta": {
                    "field": "/uuidField",
                    "invalidValue": "not a uuid"
                  }
                }
              ]
            }""");
  }

  @Test
  void invalidEnum() {
    RequestDto request = createValidRequest();
    ObjectNode node = objectMapper.valueToTree(request);
    node.remove("enumField");
    node.put("enumField", "bad enum");

    doRequestAndVerifyBody(
        node,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/enumField must be one of [FOO,BAR]",
                  "meta": {
                    "field": "/enumField",
                    "invalidValue": "bad enum"
                  }
                }
              ]
            }""");
  }

  @Test
  void fieldNonNull() {
    final var request = createValidRequest();
    request.setNestedObjectField(null);

    doRequestAndVerifyBody(
        request,
        //language=JSON
        """
            {
              "errors": [
                {
                  "code": "101",
                  "detail": "/nestedObjectField must not be null",
                  "meta": {
                    "field": "/nestedObjectField",
                    "invalidValue": null
                  }
                }
              ]
            }""");
  }

  @SneakyThrows
  private void doRequestAndVerifyBody(Object request, String body) {
    mockMvc.perform(
            post("/request-body")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().json(body));
  }
}
