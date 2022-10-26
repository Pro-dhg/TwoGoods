package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.common.util.ReportUtils;
import lombok.Data;

import java.math.BigInteger;

@Data
public class VisualizationHomeData {

    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger withInParseTotalCnt;
    private BigInteger mobileParseTotalCnt;
    private BigInteger wlanParseTotalCnt;
    private BigInteger aaaaRecordParseTotalCnt;
    private BigInteger jikeParseTotalCnt;
    private BigInteger jiakeParseTotalCnt;
    private BigInteger parseTotalCnt234g;
    private BigInteger parseTotalCnt45g;
    private BigInteger parseTotalCnt5g;

    private Double cacheSuccessRate;
    private Double netInRate;
    private Double parseInRate;
    private Double recursionSuccessRate;

    /**
     * 计算比率
     */
    public void buildRate() {
        this.cacheSuccessRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, parseTotalCnt);
        this.parseInRate = ReportUtils.buildRatioBase(withInParseTotalCnt, parseTotalCnt);
    }

    public void buildRecursionSuccessRate() {
        this.recursionSuccessRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
    }

}
