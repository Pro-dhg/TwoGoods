package com.yamu.data.sample.service.resources.entity.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class UserDistributionRateVO {

    private BigInteger value;

    private String name;

    public UserDistributionRateVO () {}

    public UserDistributionRateVO (String name, BigInteger value) {
        this.name = name;
        this.value = value;
    }
}
