package com.secondhand.market.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductListVO {
    private Long id;
    private String title;
    private BigDecimal price;
    private String conditionLevel;
    private String province;
    private String city;
    private String firstImage;
    private Integer viewCount;
    private LocalDateTime createTime;
}
