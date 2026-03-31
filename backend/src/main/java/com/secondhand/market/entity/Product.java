package com.secondhand.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String conditionLevel;
    private String tradeMethod;
    private String province;
    private String city;
    private String district;
    private Integer viewCount;
    private Integer favoriteCount;
    // 状态：0-待审核 1-在售 2-已售 3-已下架
    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
