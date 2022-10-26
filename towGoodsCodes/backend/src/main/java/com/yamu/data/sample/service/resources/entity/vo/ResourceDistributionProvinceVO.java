package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * Date 2022-03-14
 */
@Data
@ApiModel
public class ResourceDistributionProvinceVO {

    @ApiModelProperty(value = "网站名称")
    private String websiteAppName;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "时间粒度")
    private String queryType;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "节点运营商code")
    private String ispCode;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "时间段时间")
    private String queryTime;

    @ApiModelProperty(value = "查询类型")
    private String qtype;

    @ApiModelProperty(value = "页数")
    protected Long offset;

    @ApiModelProperty(value = "查询数量")
    protected Long limit;

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "域名集合")
    private List<String> domainList;

    @ApiModelProperty(value = "节点名")
    private String serverNodeName;
}
