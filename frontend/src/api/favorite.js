import request from '@/utils/request'

// 添加收藏
export const addFavorite = (productId) => {
  return request({
    url: '/favorites/add',
    method: 'post',
    data: { productId }
  })
}

// 取消收藏
export const removeFavorite = (productId) => {
  return request({
    url: `/favorites/remove/${productId}`,
    method: 'delete'
  })
}

// 获取收藏列表
export const getFavoriteList = (page = 1, size = 10) => {
  return request({
    url: '/favorites/list',
    method: 'get',
    params: { page, size }
  })
}

// 检查是否已收藏
export const checkFavorite = (productId) => {
  return request({
    url: `/favorites/check/${productId}`,
    method: 'get'
  })
}

// 获取收藏列表（别名）
export const getFavorites = (page = 1, size = 10) => {
  return getFavoriteList(page, size)
}

// 取消收藏（别名）
export const cancelFavorite = (productId) => {
  return removeFavorite(productId)
}
