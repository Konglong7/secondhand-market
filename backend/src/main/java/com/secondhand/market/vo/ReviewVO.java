package com.secondhand.market.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Long reviewerId;
    private String reviewerNickname;
    private String reviewerAvatar;
    private Long sellerId;
    private Integer rating;
    private String content;
    private List<String> images;
    private List<String> tags;
    private Boolean isAnonymous;
    private Long replyId;
    private String replyContent;
    private LocalDateTime replyTime;
    private LocalDateTime createTime;
}
