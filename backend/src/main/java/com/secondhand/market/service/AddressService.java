package com.secondhand.market.service;

import com.secondhand.market.dto.AddressRequest;
import com.secondhand.market.vo.AddressVO;
import java.util.List;

public interface AddressService {
    /**
     * 添加地址
     */
    Long addAddress(AddressRequest request, Long userId);

    /**
     * 更新地址
     */
    void updateAddress(Long id, AddressRequest request, Long userId);

    /**
     * 删除地址
     */
    void deleteAddress(Long id, Long userId);

    /**
     * 获取地址详情
     */
    AddressVO getAddress(Long id, Long userId);

    /**
     * 获取用户地址列表
     */
    List<AddressVO> getUserAddresses(Long userId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long id, Long userId);
}
