package com.secondhand.market.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.market.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM payment WHERE status = 1")
    BigDecimal getTotalAmount();
}
