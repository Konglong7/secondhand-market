package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.entity.User;
import com.secondhand.market.service.UserService;
import com.secondhand.market.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<User> getUserInfo(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 隐藏敏感信息
            user.setPassword(null);
            user.setIdCard(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     * @param nickname 昵称
     * @param avatar 头像URL
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestParam(required = false) String nickname,
                                        @RequestParam(required = false) String avatar,
                                        HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getUserIdFromRequest(request);
            userService.updateUserInfo(userId, nickname, avatar);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                        @RequestParam String newPassword,
                                        HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getUserIdFromRequest(request);
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 实名认证
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @param request HTTP请求
     * @return 操作结果
     */
    @PostMapping("/verify")
    public Result<Void> verifyIdentity(@RequestParam String realName,
                                        @RequestParam String idCard,
                                        HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getUserIdFromRequest(request);
            userService.verifyIdentity(userId, realName, idCard);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
