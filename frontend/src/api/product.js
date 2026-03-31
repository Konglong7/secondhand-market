import request from '@/utils/request'

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/products/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request({
    url: `/products/detail/${id}`,
    method: 'get'
  })
}

// 发布商品
export const publishProduct = (data) => {
  return request({
    url: '/products/publish',
    method: 'post',
    data
  })
}

// 更新商品
export const updateProduct = (id, data) => {
  return request({
    url: `/products/update/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id) => {
  return request({
    url: `/products/delete/${id}`,
    method: 'delete'
  })
}

// 更新商品状态
export const updateStatus = (id, status) => {
  return request({
    url: `/products/status/${id}`,
    method: 'put',
    data: { status }
  })
}

// 获取我的发布商品
export const getMyProducts = (page = 1, size = 10) => {
  return request({
    url: '/products/my',
    method: 'get',
    params: { page, size }
  })
}

// 更新商品状态（上下架）
export const updateProductStatus = (id, status) => {
  return request({
    url: `/products/status/${id}`,
    method: 'put',
    data: { status }
  })
}
