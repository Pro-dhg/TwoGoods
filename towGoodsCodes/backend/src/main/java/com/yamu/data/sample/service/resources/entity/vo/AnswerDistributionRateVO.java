package com.yamu.data.sample.service.resources.entity.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AnswerDistributionRateVO {

    private BigInteger value;

    private String name;

    public AnswerDistributionRateVO () {}

    public AnswerDistributionRateVO (String name, BigInteger value) {
        this.name = name;
        this.value = value;
    }
}
