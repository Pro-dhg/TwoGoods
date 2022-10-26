package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import javax.validation.constraints.Size;
import java.math.BigInteger;

@Data
public class PopularCompanyTrend extends BaseRptEntity {

    @Size(max = 200, message = "公司简称不能超过200个字符")
    private String companyShortName;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    /**
     * 网内解析数
     */
    private BigInteger netInParseTotalCnt;
    /**
     * 网外解析数
     */
    private BigInteger netOutParseTotalCnt;
    /**
     * 省内解析数
     */
    private BigInteger withinParseTotalCnt;
    /**
     * 省外解析数
     */
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;

    private Long rankNumber;
    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;

    @JsonIgnore
    private String sortWay;
    @JsonIgnore
    private Integer netInRateMax;
    @JsonIgnore
    private Integer netInRateMin;
    @JsonIgnore
    private String statisticsWay;

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_company_trend_";

    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {

        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        this.parseInRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        this.parseOutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public static final String CSV_NAME = "热点公司访问趋势分析报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,公司,解析次数,IPv4解析次数,成功次数,成功率,网内次数(IPv4),本网率,出网次数(IPv4),外网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(companyShortName).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseInRate, 2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseOutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",").append("\n");
        return this.csvLine.toString();
    }

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }

    public String getOrderBy() {
        return OrderParseUtil.parse(this.sortWay, this.statisticsWay);
    }

    private String timeRange;

    @JsonIgnore
    private String queryTime;

    private String answerFirstIsp;

    public Integer getNetInRateMax() {
        if (this.netInRateMin != null && this.netInRateMax == null) {
            return 100;
        }
        return netInRateMax;
    }

    public Integer getNetInRateMin() {
        if (this.netInRateMin == null && this.netInRateMax != null) {
            return 0;
        }
        return netInRateMin;
    }

    private String serverNodeName;
}
