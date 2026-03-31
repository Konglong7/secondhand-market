<template>
  <div class="header-container">
    <div class="header-content">
      <!-- Logo -->
      <div class="logo" @click="$router.push('/')">
        <el-icon :size="28"><ShoppingBag /></el-icon>
        <span>二手交易平台</span>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <!-- 右侧导航 -->
      <div class="nav-right">
        <el-button type="primary" @click="$router.push('/publish')">
          <el-icon><Plus /></el-icon>
          发布商品
        </el-button>

        <el-badge :value="unreadCount" :hidden="!unreadCount" class="message-badge">
          <el-button :icon="ChatDotRound" circle @click="$router.push('/chat')" />
        </el-badge>

        <!-- 未登录 -->
        <template v-if="!userStore.token">
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>

        <!-- 已登录 -->
        <template v-else>
          <el-dropdown @command="handleCommand">
            <div class="user-avatar">
              <el-avatar :src="userStore.userInfo?.avatar || defaultAvatar" />
              <span class="username">{{ userStore.userInfo?.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><Document /></el-icon>
                  我的订单
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Search,
  Plus,
  ChatDotRound,
  User,
  Document,
  SwitchButton,
  ShoppingBag
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const unreadCount = ref(0)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 加载用户信息
const loadUserInfo = async () => {
  if (userStore.token && !userStore.userInfo) {
    await userStore.getUserInfo()
  }
}

// 搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({
    path: '/products',
    query: { keyword: searchKeyword.value }
  })
}

// 下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      ElMessage.success('退出成功')
      break
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.header-container {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  white-space: nowrap;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.search-input {
  width: 100%;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-badge {
  display: flex;
  align-items: center;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }

  .logo span {
    display: none;
  }

  .search-box {
    max-width: 300px;
  }

  .username {
    display: none;
  }
}
</style>
