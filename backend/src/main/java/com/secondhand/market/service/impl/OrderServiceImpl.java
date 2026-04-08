package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.BusinessException;
import com.secondhand.market.constants.OrderStatus;
import com.secondhand.market.constants.ProductStatus;
import com.secondhand.market.constants.UserStatus;
import com.secondhand.market.dto.CreateOrderRequest;
import com.secondhand.market.dto.ShipOrderRequest;
import com.secondhand.market.entity.*;
import com.secondhand.market.mapper.*;
import com.secondhand.market.service.OrderService;
import com.secondhand.market.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserAddressMapper addressMapper;
    private final UserMapper userMapper;
    private final ProductImageMapper imageMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderRequest request, Long userId) {
        // 验证商品
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (product.getStatus() != ProductStatus.ON_SHELF) {
            throw new BusinessException(400, "商品已下架");
        }
        if (product.getUserId().equals(userId)) {
            throw new BusinessException(400, "不能购买自己的商品");
        }

        // 验证买家用户状态
        User buyer = userMapper.selectById(userId);
        if (buyer == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (buyer.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 验证库存
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException(400, "库存不足");
        }

        // 验证地址
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, request.getAddressId())
                .eq(UserAddress::getUserId, userId));
        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        // 原子扣减库存，防止超卖
        int rows = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, product.getId())
                .ge(Product::getStock, request.getQuantity())
                .set(Product::getStock, product.getStock() - request.getQuantity()));
        if (rows == 0) {
            throw new BusinessException(400, "库存不足");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo("ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        order.setProductId(product.getId());
        order.setBuyerId(userId);
        order.setSellerId(product.getUserId());
        order.setAddressId(address.getId());
        order.setQuantity(request.getQuantity());
        order.setProductPrice(product.getPrice());
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(request.getQuantity())));
        order.setProductTitle(product.getTitle());
        order.setBuyerMessage(request.getBuyerMessage());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        // 获取商品主图
        ProductImage mainImage = imageMapper.selectOne(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, product.getId())
                .orderByAsc(ProductImage::getSortOrder)
                .last("LIMIT 1"));
        if (mainImage != null) {
            order.setProductImage(mainImage.getImageUrl());
        }
        orderMapper.insert(order);

        log.info("订单创建成功: orderNo={}, buyerId={}, productId={}", order.getOrderNo(), userId, product.getId());
        return order.getOrderNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(400, "订单状态不正确");
        }

        // 验证买家用户状态
        User buyer = userMapper.selectById(userId);
        if (buyer != null && buyer.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(403, "账号已被禁用");
        }

        // 验证卖家用户状态
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null && seller.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(403, "卖家账号已被禁用");
        }

        order.setStatus(OrderStatus.PENDING_SHIPMENT);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单支付成功: orderId={}, userId={}", orderId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(ShipOrderRequest request, Long userId) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT) {
            throw new BusinessException(400, "订单状态不正确");
        }

        // 验证卖家用户状态
        User seller = userMapper.selectById(userId);
        if (seller != null && seller.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(403, "账号已被禁用");
        }

        order.setStatus(OrderStatus.PENDING_RECEIVE);
        order.setExpressCompany(request.getExpressCompany());
        order.setExpressNo(request.getExpressNo());
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单发货成功: orderId={}, expressNo={}", request.getOrderId(), request.getExpressNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_RECEIVE) {
            throw new BusinessException(400, "订单状态不正确");
        }

        // 验证买家用户状态
        User buyer = userMapper.selectById(userId);
        if (buyer != null && buyer.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(403, "账号已被禁用");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("确认收货成功: orderId={}, userId={}", orderId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        // 买家或卖家都可以取消待付款订单
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权操作此订单");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException(400, "只能取消待付款订单");
        }

        // 原子恢复库存
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            productMapper.update(null, new LambdaUpdateWrapper<Product>()
                    .eq(Product::getId, order.getProductId())
                    .set(Product::getStock, product.getStock() + order.getQuantity()));
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        log.info("订单取消成功: orderId={}, userId={}", orderId, userId);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(403, "无权查看此订单");
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

        return convertToVOPage(orderPage);
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

        return convertToVOPage(orderPage);
    }

    /**
     * 批量转换订单到VO，使用批量查询优化N+1问题
     */
    private Page<OrderVO> convertToVOPage(Page<Order> orderPage) {
        if (orderPage.getRecords().isEmpty()) {
            Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }

        List<Order> orders = orderPage.getRecords();

        // 批量收集ID
        Set<Long> productIds = orders.stream().map(Order::getProductId).collect(Collectors.toSet());
        Set<Long> buyerIds = orders.stream().map(Order::getBuyerId).collect(Collectors.toSet());
        Set<Long> sellerIds = orders.stream().map(Order::getSellerId).collect(Collectors.toSet());
        Set<Long> addressIds = orders.stream().map(Order::getAddressId).collect(Collectors.toSet());

        // 批量查询
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        Map<Long, User> userMap = userMapper.selectBatchIds(
                new ArrayList<>(buyerIds).size() > new ArrayList<>(sellerIds).size()
                        ? new ArrayList<>(buyerIds)
                        : new ArrayList<>(sellerIds)).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, UserAddress> addressMap = addressMapper.selectBatchIds(addressIds).stream()
                .collect(Collectors.toMap(UserAddress::getId, a -> a));

        // 批量查询商品图片
        LambdaQueryWrapper<ProductImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.in(ProductImage::getProductId, productIds);
        List<ProductImage> images = imageMapper.selectList(imageWrapper);
        Map<Long, ProductImage> mainImageMap = images.stream()
                .collect(Collectors.groupingBy(ProductImage::getProductId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .min(Comparator.comparing(ProductImage::getSortOrder))
                                .orElse(null)
                ));

        // 批量转换
        List<OrderVO> voList = orders.stream()
                .map(order -> convertToVOWithCache(order, productMap, userMap, addressMap, mainImageMap))
                .collect(Collectors.toList());

        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private OrderVO convertToVOWithCache(Order order, Map<Long, Product> productMap,
                                          Map<Long, User> userMap, Map<Long, UserAddress> addressMap,
                                          Map<Long, ProductImage> mainImageMap) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 订单状态文本
        vo.setStatusText(OrderStatus.getText(order.getStatus()));

        // 商品信息
        Product product = productMap.get(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            ProductImage mainImage = mainImageMap.get(product.getId());
            if (mainImage != null) {
                vo.setProductImage(mainImage.getImageUrl());
            }
        }

        // 买家信息
        User buyer = userMap.get(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getUsername());
        }

        // 卖家信息
        User seller = userMap.get(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
        }

        // 地址信息
        UserAddress address = addressMap.get(order.getAddressId());
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

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 订单状态文本
        vo.setStatusText(OrderStatus.getText(order.getStatus()));

        // 商品信息
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
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
}