package com.secondhand.market.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVO {
    private Long userCount;
    private Long productCount;
    private Long orderCount;
    private BigDecimal totalAmount;
}
