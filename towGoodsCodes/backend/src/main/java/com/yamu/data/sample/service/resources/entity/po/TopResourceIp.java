package com.yamu.data.sample.service.resources.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yamu.data.sample.service.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Author yuyuan.Dong
 * @Date 2022/2/16
 * @DESC
 */
@Data
@ApiModel
public class TopResourceIp extends BaseEntity {

    @ApiModelProperty(value = "开始时间")
    private Date parseTime;
    @ApiModelProperty(value = "排行")
    private Long rankNumber;
    @ApiModelProperty(value = "资源IP")
    private String answerFirst;
    @JsonIgnore
    private String domainName;
    @JsonIgnore
    private String websiteAppName;
    @ApiModelProperty(value = "国家")
    private String userCountry;
    @ApiModelProperty(value = "省份")
    private String userProvince;
    @ApiModelProperty(value = "城市")
    private String userCity;
    @ApiModelProperty(value = "资源IP国家")
    private String answerFirstCountry;
    @ApiModelProperty(value = "资源IP省份")
    private String answerFirstProvince;
    @ApiModelProperty(value = "资源IP城市")
    private String answerFirstCity;
    @ApiModelProperty(value = "运营商")
    private String answerFirstIsp;
    @ApiModelProperty(value = "IP类型")
    private String ipType;
    @ApiModelProperty(value = "解析量")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "域名个数")
    private Long domainNameCount;
    @ApiModelProperty(value = "网站/应用个数")
    private Long websiteAppNameCount;

    @JsonIgnore
    private String analysisType;

    //辅助字段
    @JsonIgnore
    private String isOther = "false";
    @JsonIgnore
    private String queryType = "1min";
    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    @JsonIgnore
    private String queryTableList = TABLE_PREFIX + "10min";

    private String timeRange = "10min";

    //table 前缀
    private static final String TABLE_PREFIX = "rpt_resource_ip_top_";

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

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
        if (StringUtils.isNotEmpty(timeRange)) {
            this.queryTableList = TABLE_PREFIX + timeRange;
        }
    }

    @JsonIgnore
    public static final String CSV_NAME = "TopN资源IP";
    @JsonIgnore
    public static final String CSV_HEAD = "资源IP地址,省份,城市,运营商,解析次数,排行,涉及域名个数,涉及网站/应用个数\n";
    @JsonIgnore
    public StringBuffer csvLine = new StringBuffer();

    @JsonIgnore
    public String getCsvLineSting() {
        this.csvLine.setLength(0);
        this.csvLine
                .append(answerFirst).append(",")
                .append(answerFirstProvince).append(",")
                .append(answerFirstCity).append(",")
                .append(answerFirstIsp).append(",")
                .append(parseTotalCnt).append(",")
                .append(rankNumber).append(",")
                .append(domainNameCount).append(",")
                .append(websiteAppNameCount).append(",").append("\n");
        return this.csvLine.toString();
    }

    @ApiModelProperty(value = "是否排序")
    private Boolean sortFlag;
}
