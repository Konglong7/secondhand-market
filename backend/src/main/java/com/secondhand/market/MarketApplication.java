package com.secondhand.market;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.secondhand.market.mapper")
public class MarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketApplication.class, args);
        System.out.println("========================================");
        System.out.println("二手交易平台启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}