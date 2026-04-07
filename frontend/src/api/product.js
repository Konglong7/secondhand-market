import request from '@/utils/request'

// 获取商品列表
export const getProductList = (params) => {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (id) => {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

// 发布商品
export const publishProduct = (data) => {
  return request({
    url: '/product/publish',
    method: 'post',
    data
  })
}

// 更新商品
export const updateProduct = (id, data) => {
  return request({
    url: `/product/${id}`,
    method: 'put',
    data
  })
}

// 删除商品
export const deleteProduct = (id) => {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

// 商品上架
export const onShelfProduct = (id) => {
  return request({
    url: `/product/${id}/on-shelf`,
    method: 'put'
  })
}

// 商品下架
export const offShelfProduct = (id) => {
  return request({
    url: `/product/${id}/off-shelf`,
    method: 'put'
  })
}

// 获取我的发布商品
export const getMyProducts = (page = 1, size = 10) => {
  return request({
    url: '/product/my',
    method: 'get',
    params: { page, size }
  })
}

// 更新商品状态（上下架）- 兼容旧调用
export const updateProductStatus = (id, status) => {
  if (status === 1) {
    return onShelfProduct(id)
  } else {
    return offShelfProduct(id)
  }
}

// 兼容旧名称
export const updateStatus = updateProductStatus
