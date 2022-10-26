package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/03/07
 */
@Data
public class WebsiteTopNDownloadNoResourceBO {

    private String timeRange;
    private String websiteAppName;
    private String websiteType;
    private BigInteger parseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private String successRate;
    private String netOutRate;
    private String parseOutRate;
    private BigInteger aRecordParseTotalCnt;

    public void buildRate() {
        this.successRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt), 2);
        this.netOutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt), 2);
        this.parseOutRate = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt), 2);
    }
}
