package org.ujar.boot.restful.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Data
@Accessors(chain = true)
public class RequestDto {

  @Max(100)
  @Min(10)
  private int numberField;

  @Size(min = 2, max = 3)
  private List<@NotNull String> stringListField;

  @Valid
  @NotNull
  private NestedObject nestedObjectField;

  @Valid
  @NotNull
  private List<NestedObject> nestedObjectListField;

  @NotBlank
  private String stringField;

  @NotNull
  private UUID uuidField;

  @NotNull
  private StatusType enumField;

  public List<String> getStringListField() {
    return new ArrayList<>(stringListField);
  }

  public RequestDto setStringListField(List<String> stringListField) {
    this.stringListField = new ArrayList<>(stringListField);
    return this;
  }

  public List<NestedObject> getNestedObjectListField() {
    return new ArrayList<>(nestedObjectListField);
  }

  public RequestDto setNestedObjectListField(
      List<NestedObject> nestedObjectListField) {
    this.nestedObjectListField = new ArrayList<>(nestedObjectListField);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RequestDto that = (RequestDto) o;

    return new EqualsBuilder().append(numberField, that.numberField)
        .append(stringListField, that.stringListField).append(nestedObjectField, that.nestedObjectField)
        .append(nestedObjectListField, that.nestedObjectListField).append(stringField, that.stringField)
        .append(uuidField, that.uuidField).append(enumField, that.enumField).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(numberField).append(stringListField).append(nestedObjectField)
        .append(nestedObjectListField).append(stringField).append(uuidField).append(enumField).toHashCode();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NestedObject {

    @NotBlank
    private String stringField;
  }
}
