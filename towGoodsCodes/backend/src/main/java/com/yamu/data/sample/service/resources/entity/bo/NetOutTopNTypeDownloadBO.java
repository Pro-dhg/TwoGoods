package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class NetOutTopNTypeDownloadBO {
    private String domainName;
    private String answerFirstIsp;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
}
