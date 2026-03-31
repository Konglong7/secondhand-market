import request from '@/utils/request'

/**
 * 获取地址列表
 */
export const getAddressList = () => {
  return request({
    url: '/addresses/list',
    method: 'get'
  })
}

/**
 * 添加地址
 * @param {Object} data - 地址数据
 */
export const addAddress = (data) => {
  return request({
    url: '/addresses/add',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 * @param {number} id - 地址ID
 * @param {Object} data - 地址数据
 */
export const updateAddress = (id, data) => {
  return request({
    url: `/addresses/update/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 * @param {number} id - 地址ID
 */
export const deleteAddress = (id) => {
  return request({
    url: `/addresses/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 * @param {number} id - 地址ID
 */
export const setDefaultAddress = (id) => {
  return request({
    url: `/addresses/default/${id}`,
    method: 'put'
  })
}
