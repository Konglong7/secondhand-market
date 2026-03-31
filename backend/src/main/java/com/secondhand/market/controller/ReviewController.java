package com.secondhand.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.secondhand.market.common.Result;
import com.secondhand.market.dto.ReviewRequest;
import com.secondhand.market.service.ReviewService;
import com.secondhand.market.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Result<Void> createReview(@Valid @RequestBody ReviewRequest request, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        reviewService.createReview(request, userId);
        return Result.success();
    }

    @PostMapping("/{reviewId}/reply")
    public Result<Void> replyReview(@PathVariable Long reviewId,
                                     @RequestParam String replyContent,
                                     Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        reviewService.replyReview(reviewId, replyContent, userId);
        return Result.success();
    }

    @GetMapping("/product/{productId}")
    public Result<IPage<ReviewVO>> getProductReviews(@PathVariable Long productId,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        IPage<ReviewVO> reviews = reviewService.getProductReviews(productId, page, size);
        return Result.success(reviews);
    }

    @GetMapping("/user/{sellerId}")
    public Result<IPage<ReviewVO>> getUserReviews(@PathVariable Long sellerId,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        IPage<ReviewVO> reviews = reviewService.getUserReviews(sellerId, page, size);
        return Result.success(reviews);
    }

    @GetMapping("/{reviewId}")
    public Result<ReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        ReviewVO review = reviewService.getReviewDetail(reviewId);
        return Result.success(review);
    }
}
