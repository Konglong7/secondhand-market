import request from '@/utils/request'

// 添加收藏
export const addFavorite = (productId) => {
  return request({
    url: `/favorites/${productId}`,
    method: 'post'
  })
}

// 取消收藏
export const removeFavorite = (productId) => {
  return request({
    url: `/favorites/${productId}`,
    method: 'delete'
  })
}

// 获取收藏列表
export const getFavoriteList = (page = 1, size = 10) => {
  return request({
    url: '/favorites',
    method: 'get',
    params: { page, size }
  })
}

// 检查是否已收藏
export const checkFavorite = (productId) => {
  return request({
    url: `/favorites/${productId}/check`,
    method: 'get'
  })
}

// 获取收藏列表（别名）
export const getFavorites = (page, size) => {
  return getFavoriteList(page, size)
}

// 取消收藏（别名）
export const cancelFavorite = (productId) => {
  return removeFavorite(productId)
}
