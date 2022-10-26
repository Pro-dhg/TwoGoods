package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.common.utils.YamuUtils;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.common.utils.OrderParseUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/20
 * @DESC
 */
@Data
public class ResourceCdnCacheCompany extends BaseRptEntity {

    //暂时不能用，前端没加
    @ApiModelProperty(value = "时间段")
    private String intervalType ;

    //网站名
    private String websiteAppName;
    //厂商
    @Size(max = 255, message = "域名不能超过255个字符")
    private String business;
    //用户类型
    private String userType;
    //资源属性
    private String resourceType;
    private BigInteger parseTotalCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger cacheParseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    //中国移动解析次数
    private BigInteger zgydCnt;
    //电信
    private BigInteger zgdxCnt;
    //联通
    private BigInteger zgltCnt;
    //港澳台
    private BigInteger gatCnt;
    //国外
    private BigInteger outCountryCnt;
    //未知次数
    private BigInteger unKnownCnt;
    private boolean qOtherType;
    /**
     * 时间段
     */
    private String timeRange;
    /**
     * 本省运营商名称
     */
    private String businessIsp;
    private Long rankNumber;
    private Double netInRate;
    private Double netOutRate;
    private Double withinRate;
    private Double withoutRate;

    private BigInteger icpAccuracyParseTotalCnt;
    private Double icpRate;

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";
    @JsonIgnore
    private String queryCdnTable ;


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
    @JsonIgnore
    private String queryTime;

    @JsonIgnore
    public void formatFieldToOther() {
        if ("other".equals(qtype)) {
            qOtherType = true;
            this.qtype = null;
        }
    }

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_business_cdn_cache_";

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
        //本网率
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        //出网率
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, aRecordParseTotalCnt);
        //本省率
        this.withinRate = ReportUtils.buildRatioBase(withinParseTotalCnt, parseTotalCnt);
        //出省率
        this.withoutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, parseTotalCnt);
    }

    public void buildIcpRate() {
        this.icpRate = ReportUtils.buildRatioBase(icpAccuracyParseTotalCnt, parseTotalCnt);
    }

    @JsonIgnore
    public static final String CSV_NAME = "CDN、Cache厂商分析报表";
    @JsonIgnore
    public static final String CSV_HEAD_START = "时间,厂商,解析次数,ICP调度准确率,IPv4解析次数,网内次数(IPv4),本网率,出网次数(IPv4),出网率,";
    @JsonIgnore
    public static final String CSV_HEAD_MIDDLE = ",本省率,出省次数,出省率,CDN次数,CACHE次数,";
    @JsonIgnore
    public static final String CSV_HEAD_END = ",港澳台,国外,未知资源次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public static String getCsvHead(String businessIsp){
        StringBuilder sb = new StringBuilder(CSV_HEAD_START);

        switch (businessIsp){
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
    public String getCsvLineSting(String statisticsWay,String businessIsp,String startTime,String endTime) {
        this.csvLine.setLength(0);
        if(statisticsWay.equals("all")){
            this.csvLine.append(startTime+"~"+endTime).append(",");
        }else{
            this.csvLine.append(YamuUtils.getSimpleDateFormat().format(parseTime)).append(",");
        }

        this.csvLine.append(business).append(",")
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
        switch (businessIsp){
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
