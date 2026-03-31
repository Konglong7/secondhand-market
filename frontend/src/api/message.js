import request from '@/utils/request'

// 发送消息
export const sendMessage = (toUserId, content, messageType = 'TEXT', productId = null) => {
  return request({
    url: '/messages/send',
    method: 'post',
    data: {
      toUserId,
      content,
      messageType,
      productId
    }
  })
}

// 获取会话列表
export const getConversations = (page = 1, size = 20) => {
  return request({
    url: '/messages/conversations',
    method: 'get',
    params: { page, size }
  })
}

// 获取聊天记录
export const getChatHistory = (userId, page = 1, size = 50) => {
  return request({
    url: `/messages/history/${userId}`,
    method: 'get',
    params: { page, size }
  })
}

// 标记已读
export const markAsRead = (userId) => {
  return request({
    url: `/messages/read/${userId}`,
    method: 'put'
  })
}
