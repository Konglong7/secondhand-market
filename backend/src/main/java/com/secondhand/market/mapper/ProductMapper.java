package com.secondhand.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.market.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE product SET favorite_count = favorite_count + 1 WHERE id = #{productId}")
    void incrementFavoriteCount(@Param("productId") Long productId);

    @Update("UPDATE product SET favorite_count = favorite_count - 1 WHERE id = #{productId} AND favorite_count > 0")
    void decrementFavoriteCount(@Param("productId") Long productId);

    @Update("UPDATE product SET status = #{status} WHERE id = #{productId}")
    void updateProductStatus(@Param("productId") Long productId, @Param("status") Integer status);
}
