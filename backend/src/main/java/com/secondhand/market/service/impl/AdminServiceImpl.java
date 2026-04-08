package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.market.common.BusinessException;
import com.secondhand.market.dto.AdminLoginRequest;
import com.secondhand.market.dto.UserStatusRequest;
import com.secondhand.market.entity.AdminUser;
import com.secondhand.market.entity.User;
import com.secondhand.market.mapper.AdminUserMapper;
import com.secondhand.market.mapper.UserMapper;
import com.secondhand.market.mapper.ProductMapper;
import com.secondhand.market.mapper.OrderMapper;
import com.secondhand.market.mapper.PaymentMapper;
import com.secondhand.market.service.AdminService;
import com.secondhand.market.utils.JwtUtil;
import com.secondhand.market.vo.AdminLoginVO;
import com.secondhand.market.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 管理员状态：正常
     */
    private static final int ADMIN_STATUS_NORMAL = 1;
    /**
     * 管理员状态：禁用
     */
    private static final int ADMIN_STATUS_DISABLED = 0;

    @Override
    public AdminLoginVO login(AdminLoginRequest request) {
        LambdaQueryWrapper<AdminUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminUser::getUsername, request.getUsername());
        AdminUser adminUser = adminUserMapper.selectOne(wrapper);

        if (adminUser == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        if (adminUser.getStatus() != ADMIN_STATUS_NORMAL) {
            throw new BusinessException(403, "管理员账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        String token = jwtUtil.generateToken(adminUser.getId(), "admin_" + adminUser.getUsername());
        log.info("管理员登录成功: username={}", adminUser.getUsername());

        return new AdminLoginVO(token, adminUser.getId(), adminUser.getUsername(), adminUser.getNickname());
    }

    @Override
    public Page<User> getUserList(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword));
        }

        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void updateUserStatus(UserStatusRequest request) {
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setStatus(request.getStatus());
        userMapper.updateById(user);
        log.info("用户状态更新: userId={}, status={}", request.getUserId(), request.getStatus());
    }

    @Override
    public void auditProduct(Long productId, Integer status) {
        if (productMapper.selectById(productId) == null) {
            throw new BusinessException(404, "商品不存在");
        }

        productMapper.updateProductStatus(productId, status);
        log.info("商品审核: productId={}, status={}", productId, status);
    }

    @Override
    public StatisticsVO getStatistics() {
        Long userCount = userMapper.selectCount(null);
        Long productCount = productMapper.selectCount(null);
        Long orderCount = orderMapper.selectCount(null);

        BigDecimal totalAmount = paymentMapper.getTotalAmount();
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        return new StatisticsVO(userCount, productCount, orderCount, totalAmount);
    }
}