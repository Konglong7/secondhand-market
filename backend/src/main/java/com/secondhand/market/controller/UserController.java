package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.entity.User;
import com.secondhand.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户信息查看、个人信息修改、密码修改、实名认证等功能
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户公开信息
     */
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 只返回公开信息，隐藏敏感字段
        Map<String, Object> publicInfo = new HashMap<>();
        publicInfo.put("id", user.getId());
        publicInfo.put("username", user.getUsername());
        publicInfo.put("nickname", user.getNickname());
        publicInfo.put("avatar", user.getAvatar());
        publicInfo.put("creditScore", user.getCreditScore());
        publicInfo.put("isVerified", user.getIsVerified());
        return Result.success(publicInfo);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody Map<String, String> body,
                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]更新个人信息", userId);
        userService.updateUserInfo(userId, body.get("nickname"), body.get("avatar"));
        log.info("用户信息更新成功");
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body,
                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]修改密码", userId);
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        log.info("密码修改成功");
        return Result.success();
    }

    /**
     * 实名认证
     */
    @PostMapping("/verify")
    public Result<Void> verifyIdentity(@RequestBody Map<String, String> body,
                                        Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        log.info("用户[{}]进行实名认证", userId);
        userService.verifyIdentity(userId, body.get("realName"), body.get("idCard"));
        log.info("实名认证成功");
        return Result.success();
    }
}