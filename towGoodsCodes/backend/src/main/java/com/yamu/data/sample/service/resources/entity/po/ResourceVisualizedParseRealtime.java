package com.yamu.data.sample.service.resources.entity.po;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ResourceVisualizedParseRealtime {

    private String code;

    private BigInteger cntH = BigInteger.ZERO;

    private BigInteger cnt10min = BigInteger.ZERO;

    private BigInteger cnt1min = BigInteger.ZERO;
}
