import request from '@/utils/request'

// 获取通知列表
export const getNotificationList = () => {
  return request({
    url: '/notifications/list',
    method: 'get'
  })
}

// 标记通知已读
export const markNotificationRead = (notificationId) => {
  return request({
    url: `/notifications/read/${notificationId}`,
    method: 'put'
  })
}

// 获取未读通知数量
export const getUnreadCount = () => {
  return request({
    url: '/notifications/unread-count',
    method: 'get'
  })
}
