package com.secondhand.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.secondhand.market.dto.ReviewRequest;
import com.secondhand.market.vo.ReviewVO;

public interface ReviewService {

    void createReview(ReviewRequest request, Long userId);

    void replyReview(Long reviewId, String replyContent, Long userId);

    IPage<ReviewVO> getProductReviews(Long productId, Integer page, Integer size);

    IPage<ReviewVO> getUserReviews(Long sellerId, Integer page, Integer size);

    ReviewVO getReviewDetail(Long reviewId);
}
