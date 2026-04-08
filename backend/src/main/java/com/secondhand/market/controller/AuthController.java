package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.dto.LoginRequest;
import com.secondhand.market.dto.RegisterRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.service.UserService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.LoginVO;
import com.secondhand.market.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 提供用户注册、登录、获取用户信息等功能
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     * 限流：每分钟最多10次注册请求
     */
    @PostMapping("/register")
    @RateLimit(count = 10, window = 60, message = "注册请求过于频繁，请稍后再试")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册: username={}", request.getUsername());
        userService.register(request);
        log.info("用户注册成功: username={}", request.getUsername());
        return Result.success();
    }

    /**
     * 用户登录
     * 限流：每分钟最多20次登录请求
     */
    @PostMapping("/login")
    @RateLimit(count = 20, window = 60, message = "登录请求过于频繁，请稍后再试")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());
        LoginVO loginVO = userService.login(request);
        log.info("用户登录成功: username={}", request.getUsername());
        return Result.success(loginVO);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<LoginVO.UserInfoVO> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "token无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserById(userId);

        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        LoginVO.UserInfoVO userInfo = new LoginVO.UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setCreditScore(user.getCreditScore());
        userInfo.setStatus(user.getStatus());

        return Result.success(userInfo);
    }
}