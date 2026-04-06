package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.dto.SendMessageRequest;
import com.secondhand.market.service.MessageService;
import com.secondhand.market.vo.ConversationVO;
import com.secondhand.market.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public Result<MessageVO> sendMessage(@Valid @RequestBody SendMessageRequest request, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        MessageVO message = messageService.sendMessage(userId, request);
        return Result.success(message);
    }

    @GetMapping("/conversations")
    public Result<List<ConversationVO>> getConversationList(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<ConversationVO> conversations = messageService.getConversationList(userId);
        return Result.success(conversations);
    }

    @GetMapping("/history/{otherUserId}")
    public Result<List<MessageVO>> getChatHistory(@PathVariable Long otherUserId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<MessageVO> messages = messageService.getChatHistory(userId, otherUserId);
        return Result.success(messages);
    }

    @PutMapping("/read/{fromUserId}")
    public Result<Void> markAsRead(@PathVariable Long fromUserId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        messageService.markAsRead(fromUserId, userId);
        return Result.success();
    }
}
