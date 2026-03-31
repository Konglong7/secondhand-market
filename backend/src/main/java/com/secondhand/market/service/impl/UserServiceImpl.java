package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.market.dto.LoginRequest;
import com.secondhand.market.dto.RegisterRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.mapper.UserMapper;
import com.secondhand.market.service.UserService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<>();
        phoneQuery.eq(User::getPhone, request.getPhone());
        if (userMapper.selectCount(phoneQuery) > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(emailQuery) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername());
        user.setIsVerified(0);
        user.setCreditScore(100);
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);

        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建返回对象
        LoginVO.UserInfoVO userInfo = new LoginVO.UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setCreditScore(user.getCreditScore());
        userInfo.setStatus(user.getStatus());

        return new LoginVO(token, userInfo);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        return userMapper.selectOne(query);
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatar 头像URL
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, String nickname, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }

        userMapper.updateById(user);
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    /**
     * 实名认证
     * @param userId 用户ID
     * @param realName 真实姓名
     * @param idCard 身份证号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyIdentity(Long userId, String realName, String idCard) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getIsVerified() == 1) {
            throw new RuntimeException("已完成实名认证");
        }

        // 验证身份证号格式（简单验证）
        if (idCard == null || !idCard.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$")) {
            throw new RuntimeException("身份证号格式不正确");
        }

        user.setRealName(realName);
        user.setIdCard(idCard);
        user.setIsVerified(1);
        userMapper.updateById(user);
    }
}
