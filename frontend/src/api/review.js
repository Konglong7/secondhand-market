import request from '@/utils/request'

// 创建评价
export const createReview = (data) => {
  return request({
    url: '/reviews',
    method: 'post',
    data
  })
}

// 回复评价
export const replyReview = (reviewId, content) => {
  return request({
    url: `/reviews/${reviewId}/reply`,
    method: 'post',
    params: { replyContent: content }
  })
}

// 获取商品评价
export const getProductReviews = (productId, page = 1, size = 10) => {
  return request({
    url: `/reviews/product/${productId}`,
    method: 'get',
    params: { page, size }
  })
}

// 获取用户评价
export const getUserReviews = (userId, page = 1, size = 10) => {
  return request({
    url: `/reviews/user/${userId}`,
    method: 'get',
    params: { page, size }
  })
}
