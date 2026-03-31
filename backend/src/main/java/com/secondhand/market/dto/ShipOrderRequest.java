package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ShipOrderRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "快递公司不能为空")
    private String expressCompany;

    @NotBlank(message = "快递单号不能为空")
    private String expressNo;
}
