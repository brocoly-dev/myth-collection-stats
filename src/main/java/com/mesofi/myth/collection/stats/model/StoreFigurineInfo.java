package com.mesofi.myth.collection.stats.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreFigurineInfo {
  private String rawName;
  private BigDecimal retailPrice;
  private String productUrl;
  private Boolean available;
}
