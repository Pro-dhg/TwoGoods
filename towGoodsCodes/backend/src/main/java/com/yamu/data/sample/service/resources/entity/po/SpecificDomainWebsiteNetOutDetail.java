package com.yamu.data.sample.service.resources.entity.po;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;

@Data
public class SpecificDomainWebsiteNetOutDetail extends BaseRptEntity {

    private Integer domainTypeKey;
    private String domainType;
    private String websiteAppName;
    private String domainName;
    private String websiteType;
    private String userType;
    private String answerFirstIp;
    private String answerFirstProvince;
    private String answerFirstCity;
    private String answerFirstIsp;
    private BigInteger parseTotalCnt;
    private BigInteger aRecordParseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger netOutParseTotalCount;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;
    private BigInteger cacheParseTotalCnt;
    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double parseInRate;
    private Double parseOutRate;
    private Long rankNumber;
    private String statisticsWay;

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";
    @JsonIgnore
    private String queryTime;
    @JsonIgnore
    private String sortWay;
    @JsonIgnore
    private Integer netInRateMax;
    @JsonIgnore
    private Integer netInRateMin;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_specific_domain_website_net_out_detail_";

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

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }

    public void setNetOutParseTotalCount(BigInteger netOutParseTotalCount) {
        this.netOutParseTotalCount = netOutParseTotalCount;
        if(ObjectUtil.isNull(this.netOutParseTotalCnt)) {
            this.netOutParseTotalCnt = netOutParseTotalCount;
        }
    }

    private String serverNodeName;
}
