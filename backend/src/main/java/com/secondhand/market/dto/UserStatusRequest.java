package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class UserStatusRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
