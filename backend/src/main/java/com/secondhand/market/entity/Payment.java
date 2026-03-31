package com.secondhand.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment")
public class Payment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String paymentNo;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;

    // 支付状态：0-待支付 1-支付成功 2-支付失败
    private Integer status;

    // 支付方式：1-支付宝 2-微信 3-银行卡
    private Integer paymentMethod;

    private String transactionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
