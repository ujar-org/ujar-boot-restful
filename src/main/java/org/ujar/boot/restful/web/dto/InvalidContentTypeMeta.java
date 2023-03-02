package org.ujar.boot.restful.web.dto;

import java.util.ArrayList;
import java.util.List;

public record InvalidContentTypeMeta(String invalidType, List<String> supportedTypes) {

  public List<String> getSupportedTypes() {
    return new ArrayList<>(supportedTypes);
  }
}
