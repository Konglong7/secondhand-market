package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.dto.CreateOrderRequest;
import com.secondhand.market.dto.ShipOrderRequest;
import com.secondhand.market.entity.*;
import com.secondhand.market.mapper.*;
import com.secondhand.market.service.OrderService;
import com.secondhand.market.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserAddressMapper addressMapper;
    private final UserMapper userMapper;
    private final ProductImageMapper imageMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(0, "待付款");
        STATUS_MAP.put(1, "待发货");
        STATUS_MAP.put(2, "待收货");
        STATUS_MAP.put(3, "已完成");
        STATUS_MAP.put(4, "已取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderRequest request, Long userId) {
        // 验证商品
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (product.getUserId().equals(userId)) {
            throw new RuntimeException("不能购买自己的商品");
        }

        // 验证库存
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("库存不足");
        }

        // 验证地址
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, request.getAddressId())
                .eq(UserAddress::getUserId, userId));
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }

        // 锁定库存
        int lockedStock = product.getStock() - request.getQuantity();
        product.setStock(lockedStock);
        productMapper.updateById(product);

        // 创建订单
        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis() + (int)(Math.random() * 10000));
        order.setProductId(product.getId());
        order.setBuyerId(userId);
        order.setSellerId(product.getUserId());
        order.setAddressId(address.getId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice().multiply(new BigDecimal(request.getQuantity())));
        order.setBuyerMessage(request.getBuyerMessage());
        order.setStatus(0); // 待付款
        orderMapper.insert(order);

        return order.getOrderNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long userId) {
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

        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(ShipOrderRequest request, Long userId) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus(2); // 待收货
        order.setExpressCompany(request.getExpressCompany());
        order.setExpressNo(request.getExpressNo());
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消待付款订单");
        }

        // 恢复库存
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStock(product.getStock() + order.getQuantity());
            productMapper.updateById(product);
        }

        order.setStatus(4); // 已取消
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }

        return convertToVO(order);
    }

    @Override
    public Page<OrderVO> getBuyerOrders(Long userId, Integer status, Integer page, Integer size) {
        Page<Order> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, userId)
                .orderByDesc(Order::getCreateTime);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        orderPage = orderMapper.selectPage(orderPage, wrapper);

        Page<OrderVO> voPage = new Page<>(page, size, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 订单状态文本
        vo.setStatusText(STATUS_MAP.get(order.getStatus()));

        // 商品信息
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            // 获取商品主图（第一张图片）
            ProductImage image = imageMapper.selectOne(new LambdaQueryWrapper<ProductImage>()
                    .eq(ProductImage::getProductId, product.getId())
                    .orderByAsc(ProductImage::getSortOrder)
                    .last("LIMIT 1"));
            if (image != null) {
                vo.setProductImage(image.getImageUrl());
            }
        }

        // 买家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getUsername());
        }

        // 卖家信息
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
        }

        // 地址信息
        UserAddress address = addressMapper.selectById(order.getAddressId());
        if (address != null) {
            vo.setReceiverName(address.getReceiverName());
            vo.setReceiverPhone(address.getReceiverPhone());
            vo.setFullAddress(address.getProvince() + address.getCity() +
                    address.getDistrict() + address.getDetailAddress());
        }

        // 时间格式化
        vo.setCreateTime(order.getCreateTime().format(FORMATTER));
        if (order.getPayTime() != null) {
            vo.setPayTime(order.getPayTime().format(FORMATTER));
        }
        if (order.getShipTime() != null) {
            vo.setShipTime(order.getShipTime().format(FORMATTER));
        }
        if (order.getReceiveTime() != null) {
            vo.setReceiveTime(order.getReceiveTime().format(FORMATTER));
        }
        if (order.getCancelTime() != null) {
            vo.setCancelTime(order.getCancelTime().format(FORMATTER));
        }

        return vo;
    }

    @Override
    public Page<OrderVO> getSellerOrders(Long userId, Integer status, Integer page, Integer size) {
        Page<Order> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getSellerId, userId)
                .orderByDesc(Order::getCreateTime);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        orderPage = orderMapper.selectPage(orderPage, wrapper);

        Page<OrderVO> voPage = new Page<>(page, size, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));

        return voPage;
    }
}
