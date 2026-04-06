package com.secondhand.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.secondhand.market.common.Result;
import com.secondhand.market.entity.Product;
import com.secondhand.market.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    public Result<Void> addFavorite(@PathVariable Long productId, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        favoriteService.addFavorite(productId, userId);
        return Result.success();
    }

    @DeleteMapping("/{productId}")
    public Result<Void> removeFavorite(@PathVariable Long productId, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        favoriteService.removeFavorite(productId, userId);
        return Result.success();
    }

    @GetMapping
    public Result<IPage<Product>> getFavoriteList(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        IPage<Product> favorites = favoriteService.getFavoriteList(userId, page, size);
        return Result.success(favorites);
    }

    @GetMapping("/{productId}/check")
    public Result<Boolean> isFavorite(@PathVariable Long productId, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        Boolean isFavorite = favoriteService.isFavorite(productId, userId);
        return Result.success(isFavorite);
    }
}
