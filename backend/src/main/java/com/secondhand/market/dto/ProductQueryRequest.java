package com.secondhand.market.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductQueryRequest {
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String conditionLevel;
    private String province;
    private String city;

    private Integer page = 1;
    private Integer size = 10;
}
