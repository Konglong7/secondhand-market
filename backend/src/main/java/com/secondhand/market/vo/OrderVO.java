package com.secondhand.market.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long productId;
    private String productTitle;
    private String productImage;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String buyerMessage;
    private Integer status;
    private String statusText;
    private String expressCompany;
    private String expressNo;
    private String payTime;
    private String shipTime;
    private String receiveTime;
    private String cancelTime;
    private String createTime;

    // 收货地址信息
    private String receiverName;
    private String receiverPhone;
    private String fullAddress;
}
