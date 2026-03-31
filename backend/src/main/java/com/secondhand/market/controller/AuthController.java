package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.dto.LoginRequest;
import com.secondhand.market.dto.RegisterRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.service.UserService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginVO loginVO = userService.login(request);
            return Result.success(loginVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<LoginVO.UserInfoVO> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 去除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 验证token
            if (!jwtUtil.validateToken(token)) {
                return Result.error(401, "token无效或已过期");
            }

            // 获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId);

            if (user == null) {
                return Result.error(404, "用户不存在");
            }

            // 构建用户信息
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
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
