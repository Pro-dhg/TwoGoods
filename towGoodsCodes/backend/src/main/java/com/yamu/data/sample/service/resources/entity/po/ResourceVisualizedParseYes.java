package com.yamu.data.sample.service.resources.entity.po;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ResourceVisualizedParseYes {

    private String parseTime;

    private BigInteger totalCnt = BigInteger.ZERO;

    private BigInteger wlanCnt = BigInteger.ZERO;

    private BigInteger mobileCnt = BigInteger.ZERO;
}
