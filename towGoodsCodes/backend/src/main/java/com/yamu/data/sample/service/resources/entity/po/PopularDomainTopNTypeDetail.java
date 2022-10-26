package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @Author ys.Ding
 * @Date 2021/10/28
 * @DESC
 */
@Data
public class PopularDomainTopNTypeDetail extends BaseRptEntity {

    @Size(max = 200, message = "分类名称不能超过200个字符")
    private String domainType;
    private String businessIp;
    private String userType;

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

    //辅助字段
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    @JsonIgnore
    private String queryTime;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_popular_domain_topn_type_detail_";

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
    }

    public BigInteger getARecordParseTotalCnt() {
        return aRecordParseTotalCnt;
    }

    @JsonSetter("aRecordParseTotalCnt")
    public void setARecordParseTotalCnt(BigInteger aRecordParseTotalCnt) {
        this.aRecordParseTotalCnt = aRecordParseTotalCnt;
    }

    private String domainName;
    private String answerFirstIp;
    private String answerFirstProvince;
    private String answerFirstCity;
    private String answerFirstIsp;

    //辅助字段
    @JsonIgnore
    private String aQueryTable = TABLE_PREFIX_A + queryType;

    //table 前缀
    private static final String TABLE_PREFIX_A = "rpt_resource_popular_domain_topn_type_";

    /**
     * set--queryType,拼出queryTable
     *
     * @param queryType
     */
    public void setAQueryTable(String queryType) {

        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.aQueryTable = TABLE_PREFIX_A + queryType;
        }
    }

    private String serverNodeName;

}
