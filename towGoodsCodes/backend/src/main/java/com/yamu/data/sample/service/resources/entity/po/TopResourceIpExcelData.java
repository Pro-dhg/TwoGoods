package com.yamu.data.sample.service.resources.entity.po;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TopResourceIpExcelData {

    private String answerFirst;

    private String answerFirstProvince;

    private String answerFirstCity;

    private String answerFirstIsp;

    private BigInteger parseTotalCnt;

    private Long rankNumber;

    private Long domainNameCount;

    private Long websiteAppNameCount;
}
