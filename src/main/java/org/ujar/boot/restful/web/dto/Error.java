package org.ujar.boot.restful.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Error<M> {
  private String code;
  private String detail;
  private M meta;
}
