import request from '@/utils/request'

/**
 * 用户登录
 * @param {string} username - 用户名
 * @param {string} password - 密码
 */
export const login = (username, password) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username, password }
  })
}

/**
 * 用户注册
 * @param {string} username - 用户名
 * @param {string} password - 密码
 * @param {string} phone - 手机号
 * @param {string} email - 邮箱
 */
export const register = (username, password, phone, email) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data: { username, password, phone, email }
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}
