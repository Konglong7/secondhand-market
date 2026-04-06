package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.dto.AddressRequest;
import com.secondhand.market.service.AddressService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public Result<Long> addAddress(@Validated @RequestBody AddressRequest request,
                                    HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        Long id = addressService.addAddress(request, userId);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id,
                                       @Validated @RequestBody AddressRequest request,
                                       HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        addressService.updateAddress(id, request, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id,
                                       HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        addressService.deleteAddress(id, userId);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressVO> getAddress(@PathVariable Long id,
                                         HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        AddressVO address = addressService.getAddress(id, userId);
        return Result.success(address);
    }

    @GetMapping("/list")
    public Result<List<AddressVO>> getUserAddresses(HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        List<AddressVO> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id,
                                           HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        addressService.setDefaultAddress(id, userId);
        return Result.success();
    }
}
