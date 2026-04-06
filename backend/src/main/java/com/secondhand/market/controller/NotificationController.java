package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.service.NotificationService;
import com.secondhand.market.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<List<NotificationVO>> getNotificationList(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<NotificationVO> notifications = notificationService.getNotificationList(userId);
        return Result.success(notifications);
    }

    @PutMapping("/read/{notificationId}")
    public Result<Void> markAsRead(@PathVariable Long notificationId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAsRead(notificationId, userId);
        return Result.success();
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Integer count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }
}
