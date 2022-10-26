package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;

import java.math.BigInteger;

@Data
public class WebsiteTopNTypeDetailListDownloadBO {
    private String domainType;
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
    private String withinRate;
    private String withoutRate;

    private BigInteger icpAccuracyParseTotalCnt;
    private String icpRate;

    /**
     * 计算比率
     */
    public void buildRate() {
        //成功率
        this.successRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt), 2);
        //本网率
        this.netInRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt), 2);
        //出网率
        this.netOutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt), 2);
        //本省率
        this.withinRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt), 2);
        //出省率
        this.withoutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt), 2);
        this.icpRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt), 2);
    }

}
