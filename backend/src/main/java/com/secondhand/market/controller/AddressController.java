package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.dto.AddressRequest;
import com.secondhand.market.service.AddressService;
import com.secondhand.market.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 * 提供地址的增删改查等功能
 */
@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 添加收货地址
     */
    @PostMapping
    public Result<Long> addAddress(@Validated @RequestBody AddressRequest request,
                                    Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long id = addressService.addAddress(request, userId);
        return Result.success(id);
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id,
                                       @Validated @RequestBody AddressRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.updateAddress(id, request, userId);
        return Result.success();
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(id, userId);
        return Result.success();
    }

    /**
     * 获取单个地址详情
     */
    @GetMapping("/{id}")
    public Result<AddressVO> getAddress(@PathVariable Long id,
                                         Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AddressVO address = addressService.getAddress(id, userId);
        return Result.success(address);
    }

    /**
     * 获取用户所有地址
     */
    @GetMapping("/list")
    public Result<List<AddressVO>> getUserAddresses(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<AddressVO> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id,
                                           Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.setDefaultAddress(id, userId);
        return Result.success();
    }
}