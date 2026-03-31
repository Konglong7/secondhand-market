package com.secondhand.market.service;

import com.secondhand.market.dto.AdminLoginRequest;
import com.secondhand.market.dto.UserStatusRequest;
import com.secondhand.market.entity.User;
import com.secondhand.market.vo.AdminLoginVO;
import com.secondhand.market.vo.StatisticsVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface AdminService {
    AdminLoginVO login(AdminLoginRequest request);

    Page<User> getUserList(Integer page, Integer size, String keyword);

    void updateUserStatus(UserStatusRequest request);

    void auditProduct(Long productId, Integer status);

    StatisticsVO getStatistics();
}
