package com.yamu.data.sample.service.resources.entity.po;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.common.excel.StringUtils;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.math.BigInteger;

@Data
public class ResourceCdnCacheDomain extends BaseRptEntity {

    //暂时不能用，前端没加
    @ApiModelProperty(value = "时间段")
    private String intervalType ;

    //网站名
    private String websiteAppName;

    /**
     * 域名
     */
    @Size(max = 255, message = "域名不能超过255个字符")
    private String domainName;
    /**
     * 本省运营商名称
     */
    private String businessIsp;
    /**
     * 解析总数
     */
    private BigInteger parseTotalCnt;
    /**
     * 解析成功数
     */
    private BigInteger parseSuccessCnt;
    /**
     * 解析成功率
     */
    private Double successRate;
    /**
     * 网内解析数
     */
    private BigInteger netInParseTotalCnt;
    /**
     * IPv4网内解析数
     */
    private BigInteger aRecordParseTotalCnt;
    /**
     * 本网率
     */
    private Double netInRate;
    /**
     * 网外解析数
     */
    private BigInteger netOutParseTotalCnt;
    /**
     * 出网率
     */
    private Double netOutRate;
    /**
     * 省内解析数
     */
    private BigInteger withinParseTotalCnt;
    /**
     * 本省率
     */
    private Double withinRate;
    /**
     * 省外解析数
     */
    private BigInteger withoutParseTotalCnt;
    /**
     * 出省率
     */
    private Double withoutRate;
    /**
     * cdn解析数
     */
    private BigInteger cdnParseTotalCnt;
    /**
     * cache解析数
     */
    private BigInteger cacheParseTotalCnt;
    /**
     * 时间段
     */
    private String timeRange;
    private BigInteger zgydCnt;
    private BigInteger zgdxCnt;
    private BigInteger zgltCnt;
    private BigInteger gatCnt;
    private BigInteger outCountryCnt;
    private BigInteger unKnownCnt;

    private BigInteger icpAccuracyParseTotalCnt;
    private Double icpRate;

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }


    /**
     * 统计方式
     */
    @JsonIgnore
    private String statisticsWay;
    @JsonIgnore
    private String sortWay;
    @JsonIgnore
    private Integer netInRateMax;
    @JsonIgnore
    private Integer netInRateMin;
    /**
     * 查询类型
     */
    @JsonIgnore
    private String qtype;
    /**
     * 资源属性
     */
    @JsonIgnore
    private String resourceType;
    /**
     * 用户类型
     */
    @JsonIgnore
    private String userType;
    @JsonIgnore
    private Long rankNumber;
    @JsonIgnore
    private String queryTime;

    private static final String TABLE_PREFIX = "rpt_resource_domain_cdn_cache_";
    private boolean qOtherType;
    /**
     * 辅助字段用来决定查询的表
     */
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";
    @JsonIgnore
    private String queryCdnTable ;

    /**
     * 拼出 queryTable
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
     * 截取运营商名称后两位
     * @param businessIsp
     */
    public void setBusinessIsp(String businessIsp) {
        String substring = businessIsp.substring(businessIsp.length()-2);
        this.businessIsp = substring;
    }
    /**
     * 计算比率
     */
    public void buildRate() {
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        this.withinRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        this.withoutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
    }

    public void buildIcpRate() {
        this.icpRate = ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public static final String CSV_NAME = "CDN、Cache域名分析报表";
    @JsonIgnore
    public static final String CSV_HEAD_START = "时间,域名,解析次数,ICP调度准确率,IPv4解析次数,网内次数(IPv4),本网率,出网次数(IPv4),出网率,";
    @JsonIgnore
    public static final String CSV_HEAD_MIDDLE = ",本省率,外省次数,出省率,CDN次数,CACHE次数,";
    @JsonIgnore
    public static final String CSV_HEAD_END = ",港澳台,国外,未知资源次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();
    @JsonIgnore
    public void formatFieldToOther() {
        if ("other".equals(qtype)) {
            qOtherType = true;
            this.qtype = null;
        }
    }

    @JsonIgnore
    public static String getCsvHead(String business){
        StringBuilder sb = new StringBuilder(CSV_HEAD_START);

//        switch (business){
//            case("移动"):
//                sb.append("本省次数(备注:本省移动)");
//                sb.append(CSV_HEAD_MIDDLE);
//                sb.append("中国电信,中国联通");
//                break;
//            case("电信"):
//                sb.append("本省次数(备注:本省电信)");
//                sb.append(CSV_HEAD_MIDDLE);
//                sb.append("中国移动,中国联通");
//                break;
//            case("联通"):
//                sb.append("本省次数(备注:本省联通)");
//                sb.append(CSV_HEAD_MIDDLE);
//                sb.append("中国移动,中国电信");
//                break;
//        }
        switch (business){
            case("移动"):
                sb.append("本省次数");
                sb.append(CSV_HEAD_MIDDLE);
                sb.append("中国联通,中国电信");
                break;
            case("电信"):
                sb.append("本省次数");
                sb.append(CSV_HEAD_MIDDLE);
                sb.append("中国移动,中国联通");
                break;
            case("联通"):
                sb.append("本省次数");
                sb.append(CSV_HEAD_MIDDLE);
                sb.append("中国移动,中国电信");
                break;
        }
        sb.append(CSV_HEAD_END);
        return sb.toString();
    }
    @JsonIgnore
    public String getCsvLineSting(String statisticsWay,String business,String startTime,String endTime) {
        this.csvLine.setLength(0);
        if(statisticsWay.equals("all")){
            this.csvLine.append(startTime+"-"+endTime).append(",");
        }else{
            this.csvLine.append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",");
        }

        this.csvLine.append(domainName).append(",")
                .append(parseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(icpRate,2)).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate,2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate,2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withinRate,2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withoutRate,2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(cacheParseTotalCnt).append(",");
        switch (business){
            case("移动"):
                this.csvLine.append(zgltCnt).append(",")
                        .append(zgdxCnt).append(",");
                break;
            case("电信"):
                this.csvLine.append(zgydCnt).append(",")
                .append(zgltCnt).append(",");
                break;
            case("联通"):
                this.csvLine.append(zgydCnt).append(",")
                .append(zgdxCnt).append(",");
                break;
        }
        this.csvLine.append(gatCnt).append(",")
                .append(outCountryCnt).append(",")
                .append(unKnownCnt)
                .append("\n");
        return this.csvLine.toString();
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

    private String resourceRange;
}
