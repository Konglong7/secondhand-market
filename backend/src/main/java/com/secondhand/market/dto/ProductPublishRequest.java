package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPublishRequest {
    @NotBlank(message = "商品标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100字符")
    private String title;

    @NotBlank(message = "商品描述不能为空")
    @Size(max = 2000, message = "描述长度不能超过2000字符")
    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotEmpty(message = "至少上传一张图片")
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> images;

    @NotBlank(message = "成色不能为空")
    private String conditionLevel;

    @NotBlank(message = "交易方式不能为空")
    private String tradeType;

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    private String city;

    private String district;
}
