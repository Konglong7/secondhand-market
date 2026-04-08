package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.BusinessException;
import com.secondhand.market.constants.OrderStatus;
import com.secondhand.market.dto.ReviewRequest;
import com.secondhand.market.entity.Order;
import com.secondhand.market.entity.Review;
import com.secondhand.market.mapper.OrderMapper;
import com.secondhand.market.mapper.ReviewMapper;
import com.secondhand.market.service.ReviewService;
import com.secondhand.market.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评价服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReview(ReviewRequest request, Long userId) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(403, "只能评价自己的订单");
        }
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException(400, "只有已完成的订单才能评价");
        }

        // 检查是否已评价
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, request.getOrderId());
        Long count = reviewMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(400, "该订单已评价");
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
        log.info("评价创建成功: reviewId={}, orderId={}, userId={}", review.getId(), request.getOrderId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long reviewId, String replyContent, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评价不存在");
        }
        if (!review.getSellerId().equals(userId)) {
            throw new BusinessException(403, "只能回复自己收到的评价");
        }
        if (review.getReplyContent() != null) {
            throw new BusinessException(400, "已回复过该评价");
        }

        review.setReplyId(userId);
        review.setReplyContent(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
        log.info("评价回复成功: reviewId={}, userId={}", reviewId, userId);
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