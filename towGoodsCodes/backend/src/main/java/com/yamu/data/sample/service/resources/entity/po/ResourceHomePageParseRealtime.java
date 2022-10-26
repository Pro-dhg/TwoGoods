package com.yamu.data.sample.service.resources.entity.po;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author getiejun
 * @date 2021/10/19
 */
@Data
public class ResourceHomePageParseRealtime {

    private String code;

    private BigInteger cntD = BigInteger.ZERO;

    private BigInteger cntH = BigInteger.ZERO;

    private BigInteger cnt10min = BigInteger.ZERO;

    private BigInteger cnt1min = BigInteger.ZERO;

}
