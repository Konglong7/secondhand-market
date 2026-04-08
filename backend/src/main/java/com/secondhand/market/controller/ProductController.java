package com.secondhand.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.annotation.RateLimit;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.ProductPublishRequest;
import com.secondhand.market.dto.ProductQueryRequest;
import com.secondhand.market.service.ProductService;
import com.secondhand.market.vo.ProductListVO;
import com.secondhand.market.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 * 提供商品发布、编辑、删除、上下架、查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 发布商品
     * 限流：每分钟最多10次发布请求
     */
    @PostMapping("/publish")
    @RateLimit(count = 10, window = 60, message = "商品发布过于频繁，请稍后再试")
    public Result<Long> publishProduct(@Validated @RequestBody ProductPublishRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]发布商品: title={}", userId, request.getTitle());
        Long productId = productService.publishProduct(request, userId);
        log.info("商品发布成功: productId={}", productId);
        return Result.success(productId);
    }

    /**
     * 编辑商品
     */
    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id,
                                      @Validated @RequestBody ProductPublishRequest request,
                                      Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]编辑商品: productId={}", userId, id);
        productService.updateProduct(id, request, userId);
        return Result.success();
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id,
                                      Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]删除商品: productId={}", userId, id);
        productService.deleteProduct(id, userId);
        return Result.success();
    }

    /**
     * 商品上架
     */
    @PutMapping("/{id}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long id,
                                Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.onShelf(id, userId);
        return Result.success();
    }

    /**
     * 商品下架
     */
    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id,
                                 Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.offShelf(id, userId);
        return Result.success();
    }

    /**
     * 商品列表查询
     */
    @GetMapping("/list")
    public Result<Page<ProductListVO>> queryProducts(ProductQueryRequest request) {
        Page<ProductListVO> page = productService.queryProducts(request);
        return Result.success(page);
    }

    /**
     * 商品详情
     */
    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        ProductVO productVO = productService.getProductDetail(id);
        return Result.success(productVO);
    }

    /**
     * 获取当前用户发布的商品列表
     */
    @GetMapping("/my")
    public Result<Page<ProductListVO>> getMyProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<ProductListVO> result = productService.getUserProducts(userId, page, size);
        return Result.success(result);
    }
}