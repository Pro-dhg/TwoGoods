package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.List;

@Data
public class ResourceDomainTopnDetail extends BaseRptEntity {

    private String qtype;
    private Long rankNumber;
    private String businessIp;
    private String domainName;
    private String userType;
    private String sortWay;
    /**
     * 是否按照TopN查询.
     */
    private Boolean isTopN;
    /**
     * 域名集合
     */
    private List<String> domainList;

    @JsonIgnore
    private String queryTime;
    /**
     * 查询方式
     */
    private String statisticsWay;
    /**
     * 返回时间列(根据时间粒度时:展示每一个时间粒度  根据时间段时:展示时间段)
     */
    private String timeRange;
    // 总量
    private BigInteger parseTotalCnt;
    // A 记录解析量
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger parseFailCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withOutParseTotalCnt;
    private BigInteger withInParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger cacheParseTotalCnt;
    private BigInteger idcParseTotalCnt;
    // 入网率最大值
    private Integer netInRateMax;
    //入网率最小值
    private Integer netInRateMin;

    private BigInteger icpAccuracyParseTotalCnt;
    private String companyShortName;
    private Double icpRate;

    @JsonIgnore
    private List<String> domainParamList;
    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";


    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_domain_topn_detail_";


    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setQueryType(String queryType) {
        this.queryType = queryType;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }


    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private Double successRate;

    private String successRateStr;
    private String netOutRateStr;
    private String parseOutRateStr;


    public void setRankNumber(Long rankNumber) {
        if (rankNumber == null || rankNumber == 0L) {
        } else {
            this.rankNumber = rankNumber;
        }
    }

    @JsonIgnore
    private boolean otherQtype = false;

    public void setQtype(String qtype) {
        if (StringUtils.isEmpty(qtype)) {
        } else if ("other".equals(qtype)) {
            otherQtype = true;
        } else {
            this.qtype = qtype;
        }
    }

    /**
     * 计算比率
     */
    public void buildRate() {
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt);
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        this.parseInRate = ReportUtils.buildRatioBase(withInParseTotalCnt, parseTotalCnt);
        this.parseOutRate = ReportUtils.buildRatioBase(withOutParseTotalCnt, parseTotalCnt);
        this.icpRate = ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt);
    }

    /**
     * 计算比率
     */
    public void buildRateNoResource() {
        this.successRateStr = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(parseSuccessCnt, parseTotalCnt), 2);
        this.netOutRateStr = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt), 2);
        this.parseOutRateStr = StrUtils.convertDoubleToPercent(ReportUtils.buildRatioBase(withOutParseTotalCnt, parseTotalCnt), 2);
    }

    /**
     * csv文件名.
     */
    public static final String CSV_NAME = "热点域名TopN明细分析";

    public static final String CSV_HEAD = "时间,主机ip,排名,域名,区域code,国家,省份,城市,区域,运营商,运营商code,请求方式,解析总量,成功总量,失败总量,网内总量,网外总量,省内总量,省外总量,CDN总量,CACHE总量,IDC总量\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        csvLine
                .append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",")
                .append(businessIp).append(",")
                .append(rankNumber).append(",")
                .append(domainName).append(",")
                .append(districtsCode).append(",")
                .append(country).append(",")
                .append(province).append(",")
                .append(city).append(",")
                .append(district).append(",")
                .append(isp).append(",")
                .append(ispCode).append(",")
                .append(qtype).append(",")
                .append(parseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(parseFailCnt).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(withInParseTotalCnt).append(",")
                .append(withOutParseTotalCnt).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(cacheParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",");

        return csvLine.toString();
    }

    @JsonIgnore
    public String getCsvLineForTotal() {
        this.csvLine.setLength(0);
        csvLine
                .append(timeRange).append(",")
                .append(domainName).append(",")
                .append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(withInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseInRate, 2)).append(",")
                .append(withOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(parseOutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",")
                .append(cacheParseTotalCnt).append("\n");
        return csvLine.toString();
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

    private String answerFirstIsp;

    private String netInZero;

    private String withinZero;

    private String serverNodeName;

    private String websiteAppName;
}
