package com.yamu.data.sample.service.resources.entity.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AnswerDistributionMapDataVO {

    private Long rankNumber;

    private BigInteger parseTotalCnt;

    private String distribution;
}
