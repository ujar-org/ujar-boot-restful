package org.ujar.boot.restful.web.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PageRequestDtoTest {
  private PageRequestDto dto;

  @BeforeEach
  void setUp() {
    dto = new PageRequestDto();
  }

  @Test
  void pageShouldStoreCorrectValues() {
    assertEquals(0, dto.getPage());

    dto.setPage(100);

    assertEquals(100, dto.getPage());
  }

  @Test
  void sizeShouldStoreCorrectValues() {
    assertEquals(10, dto.getSize());

    dto.setSize(100);

    assertEquals(100, dto.getSize());
  }
}
