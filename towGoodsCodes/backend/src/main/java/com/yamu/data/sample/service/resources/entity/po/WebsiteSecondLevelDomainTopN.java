package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2021/8/18
 * @DESC
 */
@Data
public class WebsiteSecondLevelDomainTopN extends BaseRptEntity {
    @Size(max = 200, message = "网站名称不能超过200个字符")
    private String websiteAppName;
    @Size(max = 255, message = "子域名不能超过255个字符")
    private String secondLevelDomain;
    private String userType;

    private String domainName;
    private String websiteTypeKey;
    private String websiteType;
    private String secondLevelDomainName;
    private String secondLevelDomainType;
    private BigInteger secondLevelDomainParseTotalCnt;

    private BigInteger parseTotalCnt;
    private BigInteger parseSuccessCnt;
    private BigInteger netInParseTotalCnt;
    private BigInteger netOutParseTotalCnt;
    private BigInteger withinParseTotalCnt;
    private BigInteger withoutParseTotalCnt;
    private BigInteger cdnParseTotalCnt;
    private BigInteger idcParseTotalCnt;

    private Long rankNumber;
    private String statisticsWay;
    private Double successRate;
    private Double netInRate;
    private Double netOutRate;
    private Double withinRate;
    private Double withoutRate;

    private String timeRange;

    //辅助字段
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";

    @JsonIgnore
    private String queryTime;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_website_second_level_domain_topn_";

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
        this.successRate = ReportUtils.buildRatioBase(parseSuccessCnt, secondLevelDomainParseTotalCnt);
        //本网率
        this.netInRate = ReportUtils.buildRatioBase(netInParseTotalCnt, secondLevelDomainParseTotalCnt);
        //出网率
        this.netOutRate = ReportUtils.buildRatioBase(netOutParseTotalCnt, secondLevelDomainParseTotalCnt);
        //本省率
        this.withinRate = ReportUtils.buildRatioBase(withinParseTotalCnt, secondLevelDomainParseTotalCnt);
        //出省率
        this.withoutRate = ReportUtils.buildRatioBase(withoutParseTotalCnt, secondLevelDomainParseTotalCnt);
    }


    @JsonIgnore
    public static final String CSV_NAME = "TopN网站子域名明细报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,网站名称,分类,子域名,子域名标签,分类,解析次数,成功次数,成功率,出网次数,出网率,网内次数,本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(websiteAppName).append(",")
                .append(websiteType).append(",")
                .append(secondLevelDomain).append(",")
                .append(secondLevelDomainName).append(",")
                .append(secondLevelDomainType).append(",")
                .append(secondLevelDomainParseTotalCnt).append(",")
                .append(parseSuccessCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(successRate, 2)).append(",")
                .append(netOutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netOutRate, 2)).append(",")
                .append(netInParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(netInRate, 2)).append(",")
                .append(withinParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withinRate, 2)).append(",")
                .append(withoutParseTotalCnt).append(",")
                .append(StrUtils.convertDoubleToPercent(withoutRate, 2)).append(",")
                .append(cdnParseTotalCnt).append(",")
                .append(idcParseTotalCnt).append(",")
                .append("\n");
        return this.csvLine.toString();
    }

    private String serverNodeName;

}
