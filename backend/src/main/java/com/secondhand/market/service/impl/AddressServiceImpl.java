package com.secondhand.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.secondhand.market.common.BusinessException;
import com.secondhand.market.dto.AddressRequest;
import com.secondhand.market.entity.UserAddress;
import com.secondhand.market.mapper.UserAddressMapper;
import com.secondhand.market.service.AddressService;
import com.secondhand.market.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 收货地址服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserAddressMapper addressMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(AddressRequest request, Long userId) {
        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() == 1) {
            addressMapper.update(null, new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .set(UserAddress::getIsDefault, 0));
        }

        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);
        addressMapper.insert(address);
        log.info("收货地址添加成功: addressId={}, userId={}", address.getId(), userId);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long id, AddressRequest request, Long userId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() == 1) {
            addressMapper.update(null, new LambdaUpdateWrapper<UserAddress>()
                    .eq(UserAddress::getUserId, userId)
                    .ne(UserAddress::getId, id)
                    .set(UserAddress::getIsDefault, 0));
        }

        BeanUtils.copyProperties(request, address);
        addressMapper.updateById(address);
        log.info("收货地址更新成功: addressId={}, userId={}", id, userId);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        addressMapper.deleteById(id);
        log.info("收货地址删除成功: addressId={}, userId={}", id, userId);
    }

    @Override
    public AddressVO getAddress(Long id, Long userId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        return convertToVO(address);
    }

    @Override
    public List<AddressVO> getUserAddresses(Long userId) {
        List<UserAddress> addresses = addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime));

        return addresses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long id, Long userId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, id)
                .eq(UserAddress::getUserId, userId));

        if (address == null) {
            throw new BusinessException(404, "地址不存在");
        }

        // 取消其他默认地址
        addressMapper.update(null, new LambdaUpdateWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0));

        // 设置当前地址为默认
        address.setIsDefault(1);
        addressMapper.updateById(address);
        log.info("设置默认收货地址成功: addressId={}, userId={}", id, userId);
    }

    private AddressVO convertToVO(UserAddress address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        vo.setCreateTime(address.getCreateTime().format(FORMATTER));
        return vo;
    }
}