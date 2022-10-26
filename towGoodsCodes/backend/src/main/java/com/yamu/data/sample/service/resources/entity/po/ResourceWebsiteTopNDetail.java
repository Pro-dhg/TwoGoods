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
public class ResourceWebsiteTopNDetail extends BaseRptEntity {

    @Size(max = 200, message = "公司名称不能超过200个字符")
    private String websiteAppName;
    private String isProvinceInwebsite;
    private String websiteType;
    private String userType;
    private String sortWay;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;
    private String answerFirstIsp;

    private BigInteger icpAccuracyParseTotalCnt;
    private String companyShortName;
    private Double icpRate;

    private Long rankNumber;
    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;



    private String parseTimeLast;
    private String timeRange;
    @JsonIgnore
    protected String lastStartTime;
    @JsonIgnore
    protected String lastEndTime;
    @JsonIgnore
    // 入网率最大值
    private Integer netInRateMax;
    //入网率最小值
    private Integer netInRateMin;


    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_website_topn_detail_";

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

    public void buildIcpRate() {
        this.icpRate = ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public static final String CSV_NAME = "TopN网站解析明细报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,网站名称,分类,解析次数,IPv4解析次数,成功次数,成功率,出网次数(IPv4),出网率,网内次数(IPv4),本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }

    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(websiteAppName).append(",")
                .append(websiteType).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseInRate, 2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseOutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",").append("\n");
        return this.csvLine.toString();
    }

    public String getOrderBy() {
        return OrderParseUtil.parse(this.sortWay, this.statisticsWay);
    }

    private String statisticsWay;

    @JsonIgnore
    private String queryTime;

    private String netInZero;

    private String withinZero;

    private String serverNodeName;
}
