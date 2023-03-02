package org.ujar.boot.restful.web;

import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.experimental.UtilityClass;
import org.springframework.validation.FieldError;

@UtilityClass
public class RestfulErrorHandlerUtils {

  /**
   * Transforms {@link org.springframework.validation.FieldError#getField()} path representation
   * to <a href="https://opis.io/json-schema/2.x/pointers.html">json pointer format</a>
   *
   * @param fieldError - error containing path to json value
   * @return String representation of json pointer
   */
  public static String getJsonPointerField(FieldError fieldError) {
    return "/" +
           fieldError.getField()
               // we don't need closed square bracket, transform a.b[0][1] to a.b.[0[1
               .replace("]", "")
               // replace delimiter a.b.[0[1 to a/b/0/1
               .replace('.', '/')
               .replace('[', '/');
  }


  /**
   * Transforms {@link com.fasterxml.jackson.databind.JsonMappingException#getPath()} path representation
   * to <a href="https://opis.io/json-schema/2.x/pointers.html">json pointer format</a>
   *
   * @param exception - provided exception
   * @return String representation of json pointer
   */
  public static String getJsonPointerField(InvalidFormatException exception) {
    return "/" +
           exception.getPath().stream()
               .map(reference -> reference.getIndex() >= 0 // Only array element has non-negative index
                   ? Integer.toString(reference.getIndex())
                   : reference.getFieldName()
               )
               .collect(Collectors.joining("/"));
  }
}
