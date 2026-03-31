package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class SendMessageRequest {
    @NotNull(message = "接收用户ID不能为空")
    private Long toUserId;

    @NotNull(message = "消息内容不能为空")
    private String content;

    private String messageType = "text";

    private Long productId;
}
