package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.common.util.ReportUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
@ApiModel
public class ResourceWebsiteReport {

    @ApiModelProperty(value = "用户类型", example = "手机|固网|其他")
    private String userType;

    @ApiModelProperty(value = "运营商类型", example = "中国电信|中国联通|中国移动|教育网|境外|未知")
    private String answerFirstIsp;

    @ApiModelProperty(value = "网站/域名TopN数量", example = "10")
    private Long websiteRankNumber;

    @ApiModelProperty(value = "趋势图类TopN数量", example = "10000")
    private Long trendChartRankNumber;

    @ApiModelProperty(value = "域名解析量趋势图", example = "0")
    private String domainNameTrendChart;

    @ApiModelProperty(value = "topN网站本网率趋势图", example = "0")
    private String websiteTopNTrendChart;

    @ApiModelProperty(value = "topN域名本网率趋势图", example = "0")
    private String domainNameTopNTrendChart;

    @ApiModelProperty(value = "topN分类本网率趋势图", example = "0")
    private String websiteTopNTypeTrendChart;

    @ApiModelProperty(value = "各省资源分布", example = "0")
    private String answerDistributionTrendChart;

    @ApiModelProperty(value = "域名各运营商", example = "0")
    private String operatorTrendChart;

    @ApiModelProperty(value = "网站解析次数", example = "0")
    private String website;

    @ApiModelProperty(value = "topN分类本网率趋势图", example = "0")
    private String websiteType;

    @ApiModelProperty(value = "分类解析次数", example = "0")
    private String websiteDomainName;

    @ApiModelProperty(value = "公司解析次数", example = "0")
    private String company;

    @ApiModelProperty(value = "cdn域名解析次数", example = "0")
    private String cdnDomainName;

    @ApiModelProperty(value = "cdn厂商解析次数", example = "0")
    private String cdnManufacturer;

    @ApiModelProperty(value = "cdn厂商解析次数", example = "0")
    private String queryType = "1min";

    @ApiModelProperty(value = "开始时间")
    protected String startTime;

    @ApiModelProperty(value = "结束时间")
    protected String endTime;

    public void formatParseTime(String queryType, String intervalType) {
        if (StringUtils.isEmpty(endTime) || StringUtils.isEmpty(startTime)) {
            Map<String, String> timeParamMap = ReportUtils.buildTimeParam(intervalType);
            this.setStartTime(timeParamMap.get(ReportUtils.NOW_START));
            this.setEndTime(timeParamMap.get(ReportUtils.NOW_END));
        }
    }
}