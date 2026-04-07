package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.market.entity.Order;
import com.secondhand.market.entity.Payment;
import com.secondhand.market.entity.Product;
import com.secondhand.market.mapper.OrderMapper;
import com.secondhand.market.mapper.PaymentMapper;
import com.secondhand.market.mapper.ProductMapper;
import com.secondhand.market.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPayment(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setPaymentNo("PAY" + System.currentTimeMillis() + (int)(Math.random() * 10000));
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(order.getTotalPrice());
        payment.setStatus(0);
        payment.setPaymentMethod(1); // 默认支付宝
        paymentMapper.insert(payment);

        return payment.getPaymentNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void simulatePayment(String paymentNo) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));

        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }

        if (payment.getStatus() != 0) {
            throw new RuntimeException("支付状态不正确");
        }

        // 模拟支付成功
        String transactionId = "TXN" + UUID.randomUUID().toString().replace("-", "");
        paymentCallback(paymentNo, transactionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paymentCallback(String paymentNo, String transactionId) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));

        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }

        if (payment.getStatus() == 1) {
            return; // 已支付，避免重复处理
        }

        // 更新支付状态
        payment.setStatus(1);
        payment.setTransactionId(transactionId);
        paymentMapper.updateById(payment);

        // 更新订单状态
        Order order = orderMapper.selectById(payment.getOrderId());
        if (order != null && order.getStatus() == 0) {
            order.setStatus(1); // 待发货
            order.setPayTime(LocalDateTime.now());
            orderMapper.updateById(order);

            // 库存已在创建订单时扣减，此处无需重复扣减
        }
    }
}

