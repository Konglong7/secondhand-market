package com.secondhand.market.service;

public interface PaymentService {
    /**
     * 创建支付
     */
    String createPayment(Long orderId, Long userId);

    /**
     * 模拟支付
     */
    void simulatePayment(String paymentNo, Long userId);

    /**
     * 支付回调
     */
    void paymentCallback(String paymentNo, String transactionId);
}
