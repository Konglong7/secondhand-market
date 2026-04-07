package com.secondhand.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.ProductPublishRequest;
import com.secondhand.market.dto.ProductQueryRequest;
import com.secondhand.market.service.ProductService;
import com.secondhand.market.vo.ProductListVO;
import com.secondhand.market.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/publish")
    public Result<Long> publishProduct(@Validated @RequestBody ProductPublishRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long productId = productService.publishProduct(request, userId);
        return Result.success(productId);
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id,
                                      @Validated @RequestBody ProductPublishRequest request,
                                      Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.updateProduct(id, request, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id,
                                      Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.deleteProduct(id, userId);
        return Result.success();
    }

    @PutMapping("/{id}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long id,
                                Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.onShelf(id, userId);
        return Result.success();
    }

    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id,
                                 Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        productService.offShelf(id, userId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Page<ProductListVO>> queryProducts(ProductQueryRequest request) {
        Page<ProductListVO> page = productService.queryProducts(request);
        return Result.success(page);
    }

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
