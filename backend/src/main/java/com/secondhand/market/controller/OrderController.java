package com.secondhand.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.CreateOrderRequest;
import com.secondhand.market.dto.ShipOrderRequest;
import com.secondhand.market.service.OrderService;
import com.secondhand.market.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 提供订单创建、支付、发货、收货、取消等功能
 */
@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<String> createOrder(@Validated @RequestBody CreateOrderRequest request,
                                       Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]创建订单，商品ID: {}", userId, request.getProductId());
        String orderNo = orderService.createOrder(request, userId);
        log.info("订单创建成功，订单号: {}", orderNo);
        return Result.success(orderNo);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public Result<Void> payOrder(@PathVariable Long orderId,
                                  Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]支付订单，订单ID: {}", userId, orderId);
        orderService.payOrder(orderId, userId);
        log.info("订单支付成功，订单ID: {}", orderId);
        return Result.success();
    }

    /**
     * 发货
     */
    @PostMapping("/ship")
    public Result<Void> shipOrder(@Validated @RequestBody ShipOrderRequest request,
                                   Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("卖家[{}]发货，订单ID: {}, 快递单号: {}", userId, request.getOrderId(), request.getExpressNo());
        orderService.shipOrder(request, userId);
        log.info("发货成功，订单ID: {}", request.getOrderId());
        return Result.success();
    }

    /**
     * 确认收货
     */
    @PostMapping("/receive/{orderId}")
    public Result<Void> confirmReceive(@PathVariable Long orderId,
                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("买家[{}]确认收货，订单ID: {}", userId, orderId);
        orderService.confirmReceive(orderId, userId);
        log.info("确认收货成功，订单ID: {}", orderId);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId,
                                     Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]取消订单，订单ID: {}", userId, orderId);
        orderService.cancelOrder(orderId, userId);
        log.info("订单取消成功，订单ID: {}", orderId);
        return Result.success();
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long orderId,
                                           Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        OrderVO order = orderService.getOrderDetail(orderId, userId);
        return Result.success(order);
    }

    /**
     * 获取买家订单列表
     */
    @GetMapping("/buyer/list")
    public Result<Page<OrderVO>> getBuyerOrders(@RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<OrderVO> orders = orderService.getBuyerOrders(userId, status, page, size);
        return Result.success(orders);
    }

    /**
     * 获取卖家订单列表
     */
    @GetMapping("/seller/list")
    public Result<Page<OrderVO>> getSellerOrders(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<OrderVO> orders = orderService.getSellerOrders(userId, status, page, size);
        return Result.success(orders);
    }
}