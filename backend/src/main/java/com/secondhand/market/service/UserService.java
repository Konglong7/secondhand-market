package com.secondhand.market.service;

import com.secondhand.market.dto.LoginRequest;
import com.secondhand.market.dto.RegisterRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.vo.LoginVO;

public interface UserService {
    /**
     * 用户注册
     * @param request 注册请求
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录信息（包含token和用户信息）
     */
    LoginVO login(LoginRequest request);

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户实体
     */
    User getUserById(Long userId);

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户实体
     */
    User getUserByUsername(String username);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatar 头像URL
     */
    void updateUserInfo(Long userId, String nickname, String avatar);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 实名认证
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     */
    void verifyIdentity(Long userId, String realName, String idCard);
}
