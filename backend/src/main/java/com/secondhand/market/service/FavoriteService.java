package com.secondhand.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.secondhand.market.entity.Product;

public interface FavoriteService {

    void addFavorite(Long productId, Long userId);

    void removeFavorite(Long productId, Long userId);

    IPage<Product> getFavoriteList(Long userId, Integer page, Integer size);

    Boolean isFavorite(Long productId, Long userId);
}
