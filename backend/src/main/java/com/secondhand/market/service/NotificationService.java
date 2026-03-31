package com.secondhand.market.service;

import com.secondhand.market.vo.NotificationVO;

import java.util.List;

public interface NotificationService {
    /**
     * 创建通知
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型（1-订单 2-评价 3-系统）
     * @param relatedId 关联ID
     */
    void createNotification(Long userId, String title, String content, Integer type, Long relatedId);

    /**
     * 获取通知列表
     * @param userId 用户ID
     * @return 通知列表
     */
    List<NotificationVO> getNotificationList(Long userId);

    /**
     * 标记为已读
     * @param notificationId 通知ID
     * @param userId 用户ID
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 获取未读数量
     * @param userId 用户ID
     * @return 未读数量
     */
    Integer getUnreadCount(Long userId);
}
