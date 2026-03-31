package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.market.entity.Message;
import com.secondhand.market.entity.User;
import com.secondhand.market.mapper.MessageMapper;
import com.secondhand.market.mapper.UserMapper;
import com.secondhand.market.dto.SendMessageRequest;
import com.secondhand.market.service.MessageService;
import com.secondhand.market.vo.ConversationVO;
import com.secondhand.market.vo.MessageVO;
import com.secondhand.market.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public MessageVO sendMessage(Long fromUserId, SendMessageRequest request) {
        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(request.getToUserId());
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setProductId(request.getProductId());
        message.setIsRead(0);

        messageMapper.insert(message);

        MessageVO messageVO = convertToVO(message);

        chatWebSocketHandler.sendMessageToUser(request.getToUserId(), messageVO);

        return messageVO;
    }

    @Override
    public List<ConversationVO> getConversationList(Long userId) {
        return messageMapper.getConversationList(userId);
    }

    @Override
    public List<MessageVO> getChatHistory(Long userId, Long otherUserId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .and(w1 -> w1.eq(Message::getFromUserId, userId).eq(Message::getToUserId, otherUserId))
                .or(w2 -> w2.eq(Message::getFromUserId, otherUserId).eq(Message::getToUserId, userId))
        );
        wrapper.orderByAsc(Message::getCreateTime);

        List<Message> messages = messageMapper.selectList(wrapper);
        return messages.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long fromUserId, Long toUserId) {
        messageMapper.markAsRead(fromUserId, toUserId);
    }

    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);

        User fromUser = userMapper.selectById(message.getFromUserId());
        if (fromUser != null) {
            vo.setFromUsername(fromUser.getUsername());
            vo.setFromAvatar(fromUser.getAvatar());
        }

        User toUser = userMapper.selectById(message.getToUserId());
        if (toUser != null) {
            vo.setToUsername(toUser.getUsername());
            vo.setToAvatar(toUser.getAvatar());
        }

        return vo;
    }
}
