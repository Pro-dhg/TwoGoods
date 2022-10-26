package com.yamu.data.sample.service.resources.entity.bo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DomainNameTrendChartExcelBO {

    private BigInteger parseTotalCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withInParseTotalCnt;
    private BigInteger withOutParseTotalCnt;
    private String timeRange;

}
