package com.secondhand.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.CreateOrderRequest;
import com.secondhand.market.dto.ShipOrderRequest;
import com.secondhand.market.service.OrderService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result<String> createOrder(@Validated @RequestBody CreateOrderRequest request,
                                       HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        String orderNo = orderService.createOrder(request, userId);
        return Result.success(orderNo);
    }

    @PostMapping("/pay/{orderId}")
    public Result<Void> payOrder(@PathVariable Long orderId,
                                  HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        orderService.payOrder(orderId, userId);
        return Result.success();
    }

    @PostMapping("/ship")
    public Result<Void> shipOrder(@Validated @RequestBody ShipOrderRequest request,
                                   HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        orderService.shipOrder(request, userId);
        return Result.success();
    }

    @PostMapping("/receive/{orderId}")
    public Result<Void> confirmReceive(@PathVariable Long orderId,
                                        HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        orderService.confirmReceive(orderId, userId);
        return Result.success();
    }

    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId,
                                     HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        orderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId,
                                           HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        OrderVO order = orderService.getOrderDetail(orderId, userId);
        return Result.success(order);
    }

    @GetMapping("/buyer/list")
    public Result<Page<OrderVO>> getBuyerOrders(@RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        Page<OrderVO> orders = orderService.getBuyerOrders(userId, status, page, size);
        return Result.success(orders);
    }

    @GetMapping("/seller/list")
    public Result<Page<OrderVO>> getSellerOrders(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest httpRequest) {
        Long userId = JwtUtil.getUserIdFromRequest(httpRequest);
        Page<OrderVO> orders = orderService.getSellerOrders(userId, status, page, size);
        return Result.success(orders);
    }
}
