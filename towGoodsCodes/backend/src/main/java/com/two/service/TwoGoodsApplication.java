package com.two.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.two.service.mapper")
@EnableScheduling
public class TwoGoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(TwoGoodsApplication.class);
    }
}
