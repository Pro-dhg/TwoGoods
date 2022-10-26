package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class NetOutTopNTypeDetailDownloadBO {
    private String answerFirstIp;
    private String answerFirstProvince;
    private String answerFirstCity;
    private String answerFirstIsp;
    private BigInteger parseTotalCnt;
}
