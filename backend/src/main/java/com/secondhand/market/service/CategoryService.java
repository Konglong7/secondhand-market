package com.secondhand.market.service;

import com.secondhand.market.entity.Category;
import java.util.List;

public interface CategoryService {
    /**
     * 获取所有分类列表
     */
    List<Category> getAllCategories();

    /**
     * 根据父级ID获取子分类
     */
    List<Category> getCategoriesByParentId(Long parentId);
}
