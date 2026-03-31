package com.secondhand.market.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String conditionLevel;
    private String tradeType;
    private String province;
    private String city;
    private String district;
    private Integer status;
    private Integer viewCount;
    private LocalDateTime createTime;

    private List<String> images;

    private SellerInfo seller;

    @Data
    public static class SellerInfo {
        private Long id;
        private String nickname;
        private String avatar;
        private Integer creditScore;
    }
}
