import request from '@/utils/request'

/**
 * 创建订单
 * @param {Object} data - 订单数据
 */
export const createOrder = (data) => {
  return request({
    url: '/orders/create',
    method: 'post',
    data
  })
}

/**
 * 支付订单
 * @param {number} orderId - 订单ID
 * @param {string} paymentType - 支付方式 (alipay/wechat)
 */
export const payOrder = (orderId, paymentType) => {
  return request({
    url: `/orders/pay/${orderId}`,
    method: 'post',
    data: { paymentType }
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
    url: '/orders/ship',
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
    url: `/orders/receive/${orderId}`,
    method: 'post'
  })
}

/**
 * 取消订单
 * @param {number} orderId - 订单ID
 */
export const cancelOrder = (orderId) => {
  return request({
    url: `/orders/cancel/${orderId}`,
    method: 'post'
  })
}

/**
 * 获取买家订单列表
 * @param {string} status - 订单状态
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 */
export const getBuyerOrders = (status, page = 1, size = 10) => {
  return request({
    url: '/orders/buyer',
    method: 'get',
    params: { status, page, size }
  })
}

/**
 * 获取卖家订单列表
 * @param {string} status - 订单状态
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 */
export const getSellerOrders = (status, page = 1, size = 10) => {
  return request({
    url: '/orders/seller',
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
    url: `/orders/detail/${orderId}`,
    method: 'get'
  })
}
