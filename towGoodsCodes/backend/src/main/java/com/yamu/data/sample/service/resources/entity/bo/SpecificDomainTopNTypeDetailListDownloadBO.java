package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;

import java.math.BigInteger;

@Data
public class SpecificDomainTopNTypeDetailListDownloadBO {
    private String websiteType;
    private BigInteger parseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private String timeRange;

    private String successRate;
    private String netInRate;
    private String netOutRate;
    private String parseInRate;
    private String parseOutRate;

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt), 2);
        this.netInRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt), 2);
        this.netOutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt), 2);
        this.parseInRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt), 2);
        this.parseOutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt), 2);
    }
}
