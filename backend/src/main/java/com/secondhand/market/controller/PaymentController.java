package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * 提供支付创建、模拟支付、支付回调等功能
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create/{orderId}")
    public Result<String> createPayment(@PathVariable Long orderId,
                                         Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]发起支付，订单ID: {}", userId, orderId);
        String paymentNo = paymentService.createPayment(orderId, userId);
        log.info("支付订单创建成功，支付号: {}", paymentNo);
        return Result.success(paymentNo);
    }

    /**
     * 模拟支付（仅用于测试）
     */
    @PostMapping("/simulate/{paymentNo}")
    public Result<Void> simulatePayment(@PathVariable String paymentNo,
                                         Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]模拟支付，支付号: {}", userId, paymentNo);
        paymentService.simulatePayment(paymentNo, userId);
        log.info("模拟支付成功，支付号: {}", paymentNo);
        return Result.success();
    }

    /**
     * 支付回调（第三方支付网关调用）
     */
    @PostMapping("/callback")
    public Result<Void> paymentCallback(@RequestParam String paymentNo,
                                         @RequestParam String transactionId) {
        log.info("收到支付回调，支付号: {}, 交易号: {}", paymentNo, transactionId);
        paymentService.paymentCallback(paymentNo, transactionId);
        log.info("支付回调处理成功");
        return Result.success();
    }
}