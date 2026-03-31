package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class CreateOrderRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须大于0")
    private Integer quantity;

    private String buyerMessage;
}
