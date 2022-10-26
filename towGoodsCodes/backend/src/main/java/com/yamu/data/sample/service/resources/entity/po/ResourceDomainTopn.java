package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.StrUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.Data;

import java.math.BigInteger;

@Data
public class ResourceDomainTopn extends BaseRptEntity {
    // 总量
    private BigInteger parseTotalCnt;
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
    private Long rankNumber =100L;
    private String businessIp;
    // 辅助字段
    private String qtype;
    private String answerFirstIp;
    private String answerFirstProvince;
    private String answerFirstCity;
    private String answerFirstIsp;


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



// todo 之后将该部分单独封装为一个对象

    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private Double successRate;

    public void setRankNumber(Long rankNumber) {
        if (rankNumber == null || rankNumber == 0L) {
        } else {
            this.rankNumber = rankNumber;
        }
    }

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
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
    }

    /**
     * csv文件名.
     */
    public static final String CSV_NAME = "域名TopN分析";


    public static final String CSV_HEAD = "时间,主机ip,排名,区域code,国家,省份,城市,区域,运营商,运营商code,请求方式,解析总量,成功总量,失败总量,网内总量,网外总量,省内总量,省外总量,CDN总量,CACHE总量,IDC总量\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();
    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        csvLine
                .append(timeRange).append(",")
                .append(businessIp).append(",")
                .append(rankNumber).append(",")
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

    private String statisticsWay;

    private String timeRange;

    @JsonIgnore
    private String queryTime;

    private String serverNodeName;
}
