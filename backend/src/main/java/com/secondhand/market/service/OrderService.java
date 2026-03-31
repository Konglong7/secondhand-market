package com.secondhand.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.dto.CreateOrderRequest;
import com.secondhand.market.dto.ShipOrderRequest;
import com.secondhand.market.vo.OrderVO;

public interface OrderService {
    /**
     * 创建订单
     */
    String createOrder(CreateOrderRequest request, Long userId);

    /**
     * 支付订单
     */
    void payOrder(Long orderId, Long userId);

    /**
     * 发货
     */
    void shipOrder(ShipOrderRequest request, Long userId);

    /**
     * 确认收货
     */
    void confirmReceive(Long orderId, Long userId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, Long userId);

    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long orderId, Long userId);

    /**
     * 买家订单列表
     */
    Page<OrderVO> getBuyerOrders(Long userId, Integer status, Integer page, Integer size);

    /**
     * 卖家订单列表
     */
    Page<OrderVO> getSellerOrders(Long userId, Integer status, Integer page, Integer size);
}
