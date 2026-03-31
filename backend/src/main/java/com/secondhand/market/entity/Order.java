package com.secondhand.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String productTitle;
    private String productImage;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal totalPrice; // 订单总价（兼容字段）
    private String buyerMessage;

    private Long addressId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    private String expressCompany;
    private String expressNo;

    // 订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 5-退款中 6-已退款
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;
    private LocalDateTime cancelTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
