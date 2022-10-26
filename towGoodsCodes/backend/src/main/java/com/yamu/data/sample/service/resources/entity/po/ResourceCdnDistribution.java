package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @author xh.wu
 * @date 2021/10/20
 */
@Data
public class ResourceCdnDistribution extends BaseRptEntity {

    private String timeRange;

    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger domainParseTotalCnt;
    private BigInteger domainARecordParseTotalCnt;
    private BigInteger domainNetInParseTotalCnt;
    private BigInteger domainNetOutParseTotalCnt;
    private String ipAddressType;
    private String ipAddress;
    private BigInteger ipAddressParseTotalCnt;
    private String ipAddressIsp;
    private String ipAddressProvince;
    private String ipAddressCity;

    private Double netInRate;
    private Double domainNetInRate;

    @Size(max = 200, message = "公司名称不能超过200个字符")
    private String cdnCompany;
    @Size(max = 255, message = "域名不能超过255个字符")
    private String domainName;
    private String cname;
    private String userCity;
    private String userType;

    //粒度
    private String queryType;

    @JsonIgnore
    private String mainTable = MAIN_TABLE_PREFIX + "_1min";

    @JsonIgnore
    private String subTable = SUB_TABLE_PREFIX + "_1min";

    public static final String MAIN_TABLE_PREFIX = "rpt_resource_cdn_distribution";

    public static final String SUB_TABLE_PREFIX = "rpt_resource_cdn_distribution_detail";

    public void setQueryType(String queryType) {

        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.mainTable = MAIN_TABLE_PREFIX + queryType;
            this.subTable = SUB_TABLE_PREFIX + queryType;
        }
    }

    public void buildRate() {
        if (this.netInParseTotalCnt != null && aRecordParseTotalCnt != null) {
            this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, aRecordParseTotalCnt);
        }
        if (this.domainNetInParseTotalCnt != null && domainARecordParseTotalCnt != null) {
            this.domainNetInRate = ReportUtils.buildRatioBase(domainNetInParseTotalCnt, domainARecordParseTotalCnt);
        }
    }

    public static final String CSV_NAME = "CDN资源分布分析";

    //解析次数,IPv4解析次数,网内次数,本网率,
    public static final String CSV_HEAD = "时间,CDN厂商名称,域名,别名,解析次数,IPv4解析次数," +
            "本网次数,本网率,出网次数,IP地址类型,IP地址,IP地址解析次数,IP地址-运营商,IP地址-省份,IP地址-地市\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineForTotal() {
        this.csvLine.setLength(0);
        csvLine
                .append(timeRange).append(",")
                .append(cdnCompany).append(",")
                /*.append(parseTotalCnt).append(",")
                .append(aRecordParseTotalCnt).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(netInRate == null ? "" : StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")*/
                .append(domainName).append(",")
                .append(cname).append(",")
                .append(domainParseTotalCnt).append(",")
                .append(domainARecordParseTotalCnt).append(",")
                .append(domainNetInParseTotalCnt).append(",")
                .append(domainNetInRate == null ? "" : StrUtils.convertDoubleToPercent(domainNetInRate, 2)).append(",")
                .append(domainNetOutParseTotalCnt).append(",")
                .append(ipAddressType).append(",")
                .append(ipAddress).append(",")
                .append(ipAddressParseTotalCnt).append(",")
                .append(ipAddressIsp).append(",")
                .append(ipAddressProvince).append(",")
                .append(ipAddressCity).append("\n");
        return csvLine.toString();
    }

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }
}
