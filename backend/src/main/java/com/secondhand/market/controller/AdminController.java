package com.secondhand.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.AdminLoginRequest;
import com.secondhand.market.dto.UserStatusRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.service.AdminService;
import com.secondhand.market.vo.AdminLoginVO;
import com.secondhand.market.entity.Product;
import com.secondhand.market.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final ProductMapper productMapper;

    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginRequest request) {
        AdminLoginVO vo = adminService.login(request);
        return Result.success(vo);
    }

    @GetMapping("/users")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> userPage = adminService.getUserList(page, size, keyword);
        return Result.success(userPage);
    }

    @PutMapping("/users/status")
    public Result<Void> updateUserStatus(@Valid @RequestBody UserStatusRequest request) {
        adminService.updateUserStatus(request);
        return Result.success();
    }

    @PutMapping("/products/{productId}/audit")
    public Result<Void> auditProduct(
            @PathVariable Long productId,
            @RequestParam Integer status) {
        adminService.auditProduct(productId, status);
        return Result.success();
    }

    /**
     * 获取商品列表（管理员）
     */
    @GetMapping("/products")
    public Result<Page<Product>> getProductList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Product> productPage = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        productPage = productMapper.selectPage(productPage, wrapper);
        return Result.success(productPage);
    }

    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO vo = adminService.getStatistics();
        return Result.success(vo);
    }
}
