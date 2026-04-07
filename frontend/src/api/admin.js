import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建独立的管理员请求实例
const adminRequest = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 管理员请求拦截器
adminRequest.interceptors.request.use(
  config => {
    const adminToken = localStorage.getItem('adminToken')
    if (adminToken) {
      config.headers.Authorization = `Bearer ${adminToken}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 管理员响应拦截器
adminRequest.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      ElMessage.error(error.response.data.message || '请求失败')
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

// 管理员登录
export const adminLogin = (username, password) => {
  return adminRequest({
    url: '/admin/login',
    method: 'post',
    data: { username, password }
  })
}

// 获取用户列表
export const getUserList = (page = 1, size = 20) => {
  return adminRequest({
    url: '/admin/users',
    method: 'get',
    params: { page, size }
  })
}

// 更新用户状态
export const updateUserStatus = (userId, status) => {
  return adminRequest({
    url: '/admin/users/status',
    method: 'put',
    data: { userId, status }
  })
}

// 获取商品列表（管理员）
export const getProductList = (status = null, page = 1, size = 20) => {
  return adminRequest({
    url: '/admin/products',
    method: 'get',
    params: { status, page, size }
  })
}

// 审核商品
export const auditProduct = (productId, status) => {
  return adminRequest({
    url: `/admin/products/${productId}/audit`,
    method: 'put',
    params: { status }
  })
}

// 获取统计数据
export const getStatistics = () => {
  return adminRequest({
    url: '/admin/statistics',
    method: 'get'
  })
}
