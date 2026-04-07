import request from '@/utils/request'

/**
 * 创建订单
 * @param {Object} data - 订单数据
 */
export const createOrder = (data) => {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

/**
 * 支付订单
 * @param {number} orderId - 订单ID
 */
export const payOrder = (orderId) => {
  return request({
    url: `/order/pay/${orderId}`,
    method: 'post'
  })
}

/**
 * 发货
 * @param {number} orderId - 订单ID
 * @param {string} expressCompany - 快递公司
 * @param {string} expressNo - 快递单号
 */
export const shipOrder = (orderId, expressCompany, expressNo) => {
  return request({
    url: '/order/ship',
    method: 'post',
    data: { orderId, expressCompany, expressNo }
  })
}

/**
 * 确认收货
 * @param {number} orderId - 订单ID
 */
export const confirmReceive = (orderId) => {
  return request({
    url: `/order/receive/${orderId}`,
    method: 'post'
  })
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 */
export const cancelOrder = (orderId) => {
  return request({
    url: `/order/cancel/${orderId}`,
    method: 'post'
  })
}

/**
 * 获取买家订单列表
 * @param {number} status - 订单状态（Integer）
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 */
export const getBuyerOrders = (status, page = 1, size = 10) => {
  return request({
    url: '/order/buyer/list',
    method: 'get',
    params: { status, page, size }
  })
}

/**
 * 获取卖家订单列表
 * @param {number} status - 订单状态（Integer）
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 */
export const getSellerOrders = (status, page = 1, size = 10) => {
  return request({
    url: '/order/seller/list',
    method: 'get',
    params: { status, page, size }
  })
}

/**
 * 获取订单详情
 * @param {number} orderId - 订单ID
 */
export const getOrderDetail = (orderId) => {
  return request({
    url: `/order/${orderId}`,
    method: 'get'
  })
}
