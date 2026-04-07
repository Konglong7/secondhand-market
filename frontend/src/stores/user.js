import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  // 登录
  const login = async (loginData) => {
    try {
      const res = await request.post('/auth/login', loginData)
      if (res.code === 200) {
        token.value = res.data.token
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(res.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败')
      return false
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      const res = await request.post('/auth/register', registerData)
      if (res.code === 200) {
        ElMessage.success('注册成功，请登录')
        return true
      } else {
        ElMessage.error(res.message || '注册失败')
        return false
      }
    } catch (error) {
      ElMessage.error('注册失败')
      return false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await request.get('/auth/info')
      if (res.code === 200) {
        userInfo.value = res.data
        return res.data
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
  }

  return {
    token,
    userInfo,
    login,
    register,
    getUserInfo,
    logout
  }
})
