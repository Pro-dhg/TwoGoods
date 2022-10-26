package com.yamu.data.sample.service.resources.entity.vo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
public class CdnServiceQualityData {

    private String parseTime;

    private BigInteger parseTotalCnt;

    private BigInteger netInParseTotalCnt;

    private BigInteger withinParseTotalCnt;

    private BigInteger cityInParseTotalCnt;

    private BigInteger businessCnt;

    private BigInteger nodeCnt;

    private BigInteger netInNoResourcesCnt;

    private BigInteger withinNoResourcesCnt;

    private BigInteger cityAndNetInParseTotalCnt;

    private BigInteger withAndNetInParseTotalCnt;

    private String name;

    private BigInteger parseSuccessCnt;
}
