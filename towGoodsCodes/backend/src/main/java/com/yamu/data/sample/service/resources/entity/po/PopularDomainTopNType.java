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

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/23
 * @DESC
 */
@Data
public class PopularDomainTopNType extends BaseRptEntity {

    @Size(max = 200, message = "分类名称不能超过200个字符")
    private String domainType;
    private String businessIp;
    private String userType;
    private String answerFirstIsp;
    private String sortWay;

    private String statisticsWay;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger parseFailCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;

    private Long rankNumber;
    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double withinRate;
    private Double withoutRate;

    private Integer domainTypeKey;

    private String timeRange;
    // 入网率最大值
    private Integer netInRateMax;
    //入网率最小值
    private Integer netInRateMin;

    private BigInteger icpAccuracyParseTotalCnt;
    private Double icpRate;

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    @JsonIgnore
    private String queryTime;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_popular_domain_topn_type_";

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
        //成功率
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        //本网率
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        //出网率
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        //本省率
        this.withinRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        //出省率
        this.withoutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
        this.icpRate = ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt);
    }


    @JsonIgnore
    public static final String CSV_NAME = "TopN分类解析明细报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,域名类型,解析次数,IPv4解析次数,成功次数,成功率,网内次数(IPv4),本网率,出网次数(IPv4),出网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(domainType).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withinRate, 2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withoutRate, 2)).append(",")
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
