package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.BusinessException;
import com.secondhand.market.entity.Favorite;
import com.secondhand.market.entity.Product;
import com.secondhand.market.mapper.FavoriteMapper;
import com.secondhand.market.mapper.ProductMapper;
import com.secondhand.market.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long productId, Long userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Long count = favoriteMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(400, "已收藏该商品");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

        productMapper.incrementFavoriteCount(productId);
        log.info("商品收藏成功: productId={}, userId={}", productId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long productId, Long userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new BusinessException(400, "未收藏该商品");
        }

        favoriteMapper.deleteById(favorite.getId());
        productMapper.decrementFavoriteCount(productId);
        log.info("商品取消收藏: productId={}, userId={}", productId, userId);
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