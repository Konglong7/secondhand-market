package com.secondhand.market.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationVO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String lastMessage;
    private String lastMessageType;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}
