package com.secondhand.market.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageVO {
    private Long id;
    private Long fromUserId;
    private String fromUsername;
    private String fromAvatar;
    private Long toUserId;
    private String toUsername;
    private String toAvatar;
    private String content;
    private String messageType;
    private Long productId;
    private Integer isRead;
    private LocalDateTime createTime;
}
