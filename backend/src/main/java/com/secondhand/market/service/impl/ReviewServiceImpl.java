package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.dto.ReviewRequest;
import com.secondhand.market.entity.Order;
import com.secondhand.market.entity.Review;
import com.secondhand.market.mapper.OrderMapper;
import com.secondhand.market.mapper.ReviewMapper;
import com.secondhand.market.service.ReviewService;
import com.secondhand.market.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public void createReview(ReviewRequest request, Long userId) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("只能评价自己的订单");
        }
        if (order.getStatus() != 3) {
            throw new RuntimeException("只有已完成的订单才能评价");
        }

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, request.getOrderId());
        Long count = reviewMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该订单已评价");
        }

        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setProductId(order.getProductId());
        review.setReviewerId(userId);
        review.setSellerId(order.getSellerId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setIsAnonymous(request.getIsAnonymous());

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            review.setImages(String.join(",", request.getImages()));
        }
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            review.setTags(String.join(",", request.getTags()));
        }

        reviewMapper.insert(review);
    }

    @Override
    @Transactional
    public void replyReview(Long reviewId, String replyContent, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        if (!review.getSellerId().equals(userId)) {
            throw new RuntimeException("只能回复自己收到的评价");
        }
        if (review.getReplyContent() != null) {
            throw new RuntimeException("已回复过该评价");
        }

        review.setReplyId(userId);
        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    @Override
    public IPage<ReviewVO> getProductReviews(Long productId, Integer page, Integer size) {
        Page<ReviewVO> pageParam = new Page<>(page, size);
        return reviewMapper.selectProductReviews(pageParam, productId);
    }

    @Override
    public IPage<ReviewVO> getUserReviews(Long sellerId, Integer page, Integer size) {
        Page<ReviewVO> pageParam = new Page<>(page, size);
        return reviewMapper.selectUserReviews(pageParam, sellerId);
    }

    @Override
    public ReviewVO getReviewDetail(Long reviewId) {
        return reviewMapper.selectReviewDetail(reviewId);
    }
}
