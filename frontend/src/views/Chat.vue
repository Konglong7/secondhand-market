<template>
  <div class="chat-page">
    <Header />
    <div class="chat-container">
      <!-- 左侧会话列表 -->
      <div class="conversation-list">
        <div class="list-header">
          <h3>消息</h3>
        </div>
        <div class="conversations">
          <div
            v-for="conv in conversations"
            :key="conv.userId"
            :class="['conversation-item', { active: currentUserId === conv.userId }]"
            @click="selectConversation(conv.userId)"
          >
            <el-avatar :src="conv.avatar" :size="50">{{ conv.nickname?.charAt(0) }}</el-avatar>
            <div class="conv-info">
              <div class="conv-header">
                <span class="nickname">{{ conv.nickname }}</span>
                <span class="time">{{ formatTime(conv.lastMessageTime) }}</span>
              </div>
              <div class="conv-footer">
                <span class="last-message">{{ conv.lastMessage }}</span>
                <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" class="unread-badge" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧聊天窗口 -->
      <div class="chat-window">
        <div v-if="currentUserId" class="chat-content">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <el-avatar :src="currentUser?.avatar" :size="40">{{ currentUser?.nickname?.charAt(0) }}</el-avatar>
            <span class="chat-nickname">{{ currentUser?.nickname }}</span>
          </div>

          <!-- 消息列表 -->
          <div class="message-list" ref="messageListRef">
            <div
              v-for="msg in messages"
              :key="msg.id"
              :class="['message-item', msg.fromUserId === userId ? 'self' : 'other']"
            >
              <el-avatar :src="msg.avatar" :size="40">{{ msg.nickname?.charAt(0) }}</el-avatar>
              <div class="message-content">
                <div class="message-info">
                  <span class="message-nickname">{{ msg.nickname }}</span>
                  <span class="message-time">{{ formatTime(msg.createTime) }}</span>
                </div>
                <div v-if="msg.messageType === 'TEXT'" class="message-bubble">
                  {{ msg.content }}
                </div>
                <div v-else-if="msg.messageType === 'IMAGE'" class="message-image">
                  <el-image :src="msg.content" :preview-src-list="[msg.content]" fit="cover" />
                </div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="input-area">
            <div class="input-toolbar">
              <el-upload
                :action="`/api/files/upload`"
                :headers="{ Authorization: `Bearer ${token}` }"
                :show-file-list="false"
                :on-success="handleImageUpload"
                accept="image/*"
              >
                <el-button :icon="Picture" circle />
              </el-upload>
            </div>
            <div class="input-box">
              <el-input
                v-model="messageInput"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keydown.enter.exact.prevent="handleSend"
              />
              <el-button type="primary" @click="handleSend" :disabled="!messageInput.trim()">发送</el-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-chat">
          <el-empty description="选择一个会话开始聊天" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Header from '@/components/Header.vue'
import { getConversations, getChatHistory, sendMessage, markAsRead } from '@/api/message'

const conversations = ref([])
const currentUserId = ref(null)
const messages = ref([])
const messageInput = ref('')
const messageListRef = ref(null)
const ws = ref(null)
const token = localStorage.getItem('token')
const userId = ref(JSON.parse(localStorage.getItem('userInfo') || '{}').id)

const currentUser = computed(() => {
  return conversations.value.find(c => c.userId === currentUserId.value)
})

// 加载会话列表
const loadConversations = async () => {
  try {
    const res = await getConversations()
    conversations.value = res.data.list || []
  } catch (error) {
    ElMessage.error('加载会话列表失败')
  }
}

// 选择会话
const selectConversation = async (targetUserId) => {
  currentUserId.value = targetUserId
  await loadChatHistory()
  await markAsRead(targetUserId)

  // 更新未读数
  const conv = conversations.value.find(c => c.userId === targetUserId)
  if (conv) {
    conv.unreadCount = 0
  }
}

// 加载聊天记录
const loadChatHistory = async () => {
  try {
    const res = await getChatHistory(currentUserId.value)
    messages.value = res.data.list || []
    await nextTick()
    scrollToBottom()
  } catch (error) {
    ElMessage.error('加载聊天记录失败')
  }
}

// 发送消息
const handleSend = async () => {
  if (!messageInput.value.trim()) return

  try {
    await sendMessage(currentUserId.value, messageInput.value, 'TEXT')
    messageInput.value = ''
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

// 图片上传成功
const handleImageUpload = async (response) => {
  if (response.code === 200) {
    try {
      await sendMessage(currentUserId.value, response.data.url, 'IMAGE')
    } catch (error) {
      ElMessage.error('发送图片失败')
    }
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

  return `${date.getMonth() + 1}-${date.getDate()}`
}

// WebSocket连接
const connectWebSocket = () => {
  ws.value = new WebSocket(`ws://localhost:8080/api/ws/chat?token=${token}`)

  ws.value.onopen = () => {
    console.log('WebSocket连接成功')
  }

  ws.value.onmessage = (event) => {
    const data = JSON.parse(event.data)

    // 如果是当前会话的消息，添加到消息列表
    if (data.fromUserId === currentUserId.value || data.toUserId === currentUserId.value) {
      messages.value.push(data)
      nextTick(() => scrollToBottom())
    }

    // 更新会话列表
    const conv = conversations.value.find(c => c.userId === data.fromUserId)
    if (conv) {
      conv.lastMessage = data.messageType === 'TEXT' ? data.content : '[图片]'
      conv.lastMessageTime = data.createTime
      if (data.fromUserId !== currentUserId.value) {
        conv.unreadCount = (conv.unreadCount || 0) + 1
      }
      // 将会话移到最前面
      conversations.value = [conv, ...conversations.value.filter(c => c.userId !== data.fromUserId)]
    }
  }

  ws.value.onerror = () => {
    ElMessage.error('WebSocket连接失败')
  }

  ws.value.onclose = () => {
    console.log('WebSocket连接关闭')
    // 5秒后重连
    setTimeout(connectWebSocket, 5000)
  }
}

onMounted(() => {
  loadConversations()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws.value) {
    ws.value.close()
  }
})
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.chat-container {
  display: flex;
  height: calc(100vh - 60px);
  max-width: 1400px;
  margin: 0 auto;
  background: white;
}

/* 左侧会话列表 */
.conversation-list {
  width: 320px;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.list-header h3 {
  margin: 0;
  font-size: 18px;
}

.conversations {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  padding: 15px 20px;
  cursor: pointer;
  transition: background 0.3s;
  border-bottom: 1px solid #f0f0f0;
}

.conversation-item:hover {
  background: #f5f5f5;
}

.conversation-item.active {
  background: #e6f7ff;
}

.conv-info {
  flex: 1;
  margin-left: 12px;
  overflow: hidden;
}

.conv-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.nickname {
  font-weight: 500;
  font-size: 15px;
}

.time {
  font-size: 12px;
  color: #999;
}

.conv-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  flex: 1;
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧聊天窗口 */
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
}

.chat-nickname {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 500;
}

.message-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  margin-bottom: 20px;
}

.message-item.self {
  flex-direction: row-reverse;
}

.message-item.self .message-content {
  align-items: flex-end;
}

.message-item.self .message-bubble {
  background: #1890ff;
  color: white;
}

.message-content {
  display: flex;
  flex-direction: column;
  margin: 0 12px;
  max-width: 60%;
}

.message-info {
  display: flex;
  gap: 10px;
  margin-bottom: 5px;
  font-size: 12px;
  color: #999;
}

.message-bubble {
  padding: 10px 15px;
  background: #f0f0f0;
  border-radius: 8px;
  word-break: break-word;
}

.message-image {
  max-width: 300px;
}

.message-image :deep(.el-image) {
  border-radius: 8px;
  cursor: pointer;
}

.input-area {
  border-top: 1px solid #e0e0e0;
  padding: 15px 20px;
}

.input-toolbar {
  margin-bottom: 10px;
}

.input-box {
  display: flex;
  gap: 10px;
}

.input-box :deep(.el-textarea) {
  flex: 1;
}
</style>
