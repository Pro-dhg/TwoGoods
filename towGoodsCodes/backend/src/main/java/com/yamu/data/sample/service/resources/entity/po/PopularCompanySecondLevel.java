package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseRptEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Size;
import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2022/3/16
 * @DESC
 */
@Data
public class PopularCompanySecondLevel extends BaseRptEntity {

    @Size(max = 200, message = "公司名称不能超过200个字符")
    @ApiModelProperty(value = "公司名称")
    private String companyShortName;
    @Size(max = 255, message = "子域名不能超过255个字符")
    @ApiModelProperty(value = "子域名")
    private String secondLevelDomain;
    @JsonIgnore
    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "服务IP")
    private String businessIp;
    @ApiModelProperty(value = "域名")
    private String domainName;
    @ApiModelProperty(value = "公司类型key")
    private String companyTypeKey;
    @ApiModelProperty(value = "公司类型")
    private String companyType;
    @ApiModelProperty(value = "子域名公司标签名称")
    private String secondLevelDomainName;
    @ApiModelProperty(value = "分类")
    private String secondLevelDomainType;
    @ApiModelProperty(value = "子域名解析次数")
    private BigInteger secondLevelDomainParseTotalCnt;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
    @ApiModelProperty(value = "成功次数")
    private BigInteger parseSuccessCnt;
    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;
    @ApiModelProperty(value = "网外次数")
    private BigInteger netOutParseTotalCnt;
    @ApiModelProperty(value = "省内次数")
    private BigInteger withinParseTotalCnt;
    @ApiModelProperty(value = "省外次数")
    private BigInteger withoutParseTotalCnt;
    @ApiModelProperty(value = "CDN次数")
    private BigInteger cdnParseTotalCnt;
    @ApiModelProperty(value = "IDC次数")
    private BigInteger idcParseTotalCnt;

    @JsonIgnore
    @ApiModelProperty(value = "排名")
    private Long rankNumber;
    @JsonIgnore
    @ApiModelProperty(value = "统计方式")
    private String statisticsWay;
    @ApiModelProperty(value = "成功率")
    private Double successRate;
    @ApiModelProperty(value = "本网率")
    private Double netInRate;
    @ApiModelProperty(value = "出网率")
    private Double netOutRate;
    @ApiModelProperty(value = "本省率")
    private Double withinRate;
    @ApiModelProperty(value = "出省率")
    private Double withoutRate;

    @ApiModelProperty(value = "时间段")
    private String timeRange;

    //辅助字段
    @JsonIgnore
    private String queryType = "1d";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1d";

    @JsonIgnore
    private String queryTime;

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_company_second_level_domain_topn_";

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
    public static final String CSV_NAME = "TopN公司子域名明细报表";
    @JsonIgnore
    public static final String CSV_HEAD = "时间,公司名称,子域名,子域名标签,分类,解析次数,成功次数,成功率,出网次数,出网率,网内次数,本网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(timeRange).append(",")
                .append(companyShortName).append(",")
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

    @ApiModelProperty(value = "节点名")
    private String serverNodeName;
}
