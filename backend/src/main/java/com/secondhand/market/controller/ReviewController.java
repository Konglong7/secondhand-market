package com.secondhand.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.ReviewRequest;
import com.secondhand.market.service.ReviewService;
import com.secondhand.market.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评价控制器
 * 提供评价创建、回复、查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<Void> createReview(@Valid @RequestBody ReviewRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]创建评价，订单ID: {}", userId, request.getOrderId());
        reviewService.createReview(request, userId);
        log.info("评价创建成功");
        return Result.success();
    }

    /**
     * 回复评价（商家回复）
     */
    @PostMapping("/{reviewId}/reply")
    public Result<Void> replyReview(@PathVariable Long reviewId,
                                     @RequestParam String replyContent,
                                     Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("商家[{}]回复评价，评价ID: {}", userId, reviewId);
        reviewService.replyReview(reviewId, replyContent, userId);
        log.info("评价回复成功");
        return Result.success();
    }

    /**
     * 获取商品评价列表
     */
    @GetMapping("/product/{productId}")
    public Result<IPage<ReviewVO>> getProductReviews(@PathVariable Long productId,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        IPage<ReviewVO> reviews = reviewService.getProductReviews(productId, page, size);
        return Result.success(reviews);
    }

    /**
     * 获取用户（商家）收到的评价列表
     */
    @GetMapping("/user/{sellerId}")
    public Result<IPage<ReviewVO>> getUserReviews(@PathVariable Long sellerId,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        IPage<ReviewVO> reviews = reviewService.getUserReviews(sellerId, page, size);
        return Result.success(reviews);
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{reviewId}")
    public Result<ReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        ReviewVO review = reviewService.getReviewDetail(reviewId);
        return Result.success(review);
    }
}