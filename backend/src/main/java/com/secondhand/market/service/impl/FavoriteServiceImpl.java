package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.entity.Favorite;
import com.secondhand.market.entity.Product;
import com.secondhand.market.mapper.FavoriteMapper;
import com.secondhand.market.mapper.ProductMapper;
import com.secondhand.market.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public void addFavorite(Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Long count = favoriteMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

        productMapper.incrementFavoriteCount(productId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long productId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new RuntimeException("未收藏该商品");
        }

        favoriteMapper.deleteById(favorite.getId());
        productMapper.decrementFavoriteCount(productId);
    }

    @Override
    public IPage<Product> getFavoriteList(Long userId, Integer page, Integer size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .orderByDesc(Favorite::getCreateTime);
        IPage<Favorite> favoritePage = favoriteMapper.selectPage(pageParam, wrapper);

        List<Long> productIds = favoritePage.getRecords().stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return new Page<>(page, size, 0);
        }

        List<Product> products = productMapper.selectBatchIds(productIds);
        Page<Product> productPage = new Page<>(page, size, favoritePage.getTotal());
        productPage.setRecords(products);
        return productPage;
    }

    @Override
    public Boolean isFavorite(Long productId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Long count = favoriteMapper.selectCount(wrapper);
        return count > 0;
    }
}
