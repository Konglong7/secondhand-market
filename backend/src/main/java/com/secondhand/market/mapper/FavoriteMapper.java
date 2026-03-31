package com.secondhand.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.market.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
