package com.secondhand.market.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class ReviewRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Size(max = 500, message = "评价内容不能超过500字")
    private String content;

    private List<String> images;

    private List<String> tags;

    private Boolean isAnonymous = false;
}
