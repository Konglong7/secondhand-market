import request from '@/utils/request'

// 获取分类列表
export const getCategoryList = () => {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

// 获取子分类列表
export const getChildCategories = (parentId) => {
  return request({
    url: `/category/children/${parentId}`,
    method: 'get'
  })
}
