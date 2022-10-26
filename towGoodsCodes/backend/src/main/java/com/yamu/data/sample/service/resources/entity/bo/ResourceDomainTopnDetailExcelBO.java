package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ResourceDomainTopnDetailExcelBO {

    private String timeRange;
    private String domainName;
    private BigInteger parseTotalCnt;
    private BigInteger parseSuccessCnt;
    private String successRateStr;
    private BigInteger netOutParseTotalCnt;
    private String netOutRateStr;
    private BigInteger withOutParseTotalCnt;
    private String parseOutRateStr;
    private BigInteger aRecordParseTotalCnt;

}
