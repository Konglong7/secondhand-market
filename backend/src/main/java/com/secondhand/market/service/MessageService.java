package com.secondhand.market.service;

import com.secondhand.market.dto.SendMessageRequest;
import com.secondhand.market.vo.ConversationVO;
import com.secondhand.market.vo.MessageVO;

import java.util.List;

public interface MessageService {
    MessageVO sendMessage(Long fromUserId, SendMessageRequest request);

    List<ConversationVO> getConversationList(Long userId);

    List<MessageVO> getChatHistory(Long userId, Long otherUserId);

    void markAsRead(Long fromUserId, Long toUserId);
}
