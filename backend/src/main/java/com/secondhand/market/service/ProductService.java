package com.secondhand.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.dto.ProductPublishRequest;
import com.secondhand.market.dto.ProductQueryRequest;
import com.secondhand.market.entity.Product;
import com.secondhand.market.vo.ProductListVO;
import com.secondhand.market.vo.ProductVO;

public interface ProductService {
    /**
     * 发布商品
     * @param request 商品发布请求
     * @param userId 用户ID
     * @return 商品ID
     */
    Long publishProduct(ProductPublishRequest request, Long userId);

    /**
     * 编辑商品
     * @param productId 商品ID
     * @param request 商品发布请求
     * @param userId 用户ID
     */
    void updateProduct(Long productId, ProductPublishRequest request, Long userId);

    /**
     * 删除商品
     * @param productId 商品ID
     * @param userId 用户ID
     */
    void deleteProduct(Long productId, Long userId);

    /**
     * 上架商品
     * @param productId 商品ID
     * @param userId 用户ID
     */
    void onShelf(Long productId, Long userId);

    /**
     * 下架商品
     * @param productId 商品ID
     * @param userId 用户ID
     */
    void offShelf(Long productId, Long userId);

    /**
     * 分页查询商品列表
     * @param request 查询请求
     * @return 商品列表分页
     */
    Page<ProductListVO> queryProducts(ProductQueryRequest request);

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductVO getProductDetail(Long productId);

    /**
     * 获取用户发布的商品列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表分页
     */
    Page<ProductListVO> getUserProducts(Long userId, Integer page, Integer size);
}
