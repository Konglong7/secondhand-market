package com.secondhand.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.entity.Review;
import com.secondhand.market.vo.ReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    IPage<ReviewVO> selectProductReviews(Page<?> page, @Param("productId") Long productId);

    IPage<ReviewVO> selectUserReviews(Page<?> page, @Param("sellerId") Long sellerId);

    ReviewVO selectReviewDetail(@Param("id") Long id);
}
