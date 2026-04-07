import request from '@/utils/request'

// 获取用户公开信息
export const getUserInfo = (userId) => {
  return request({
    url: `/user/${userId}`,
    method: 'get'
  })
}

// 更新用户信息
export const updateUserInfo = (data) => {
  return request({
    url: '/user/info',
    method: 'put',
    params: data
  })
}

// 修改密码
export const changePassword = (oldPassword, newPassword) => {
  return request({
    url: '/user/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

// 实名认证
export const verifyIdentity = (realName, idCard) => {
  return request({
    url: '/user/verify',
    method: 'post',
    params: { realName, idCard }
  })
}
