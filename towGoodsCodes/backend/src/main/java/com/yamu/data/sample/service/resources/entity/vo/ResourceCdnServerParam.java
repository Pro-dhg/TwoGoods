package com.yamu.data.sample.service.resources.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/06/24
 */

@Data
@ApiModel
public class ResourceCdnServerParam {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时间粒度")
    private String queryType;

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "CDN厂商(输入框)")
    private String businessStr;

    @ApiModelProperty(value = "CDN厂商")
    private String business;

    @JsonIgnore
    private List<String> businessList;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "V4/V6")
    private String ipType;

    @ApiModelProperty(value = "分页码")
    private Long offset;

    @ApiModelProperty(value = "分页数量")
    private Long limit;

    @ApiModelProperty(value = "CDN域名(输入框)")
    private String domainNameStr;

    @ApiModelProperty(value = "CDN域名")
    private String domainName;

    @JsonIgnore
    private List<String> domainNameList;
}
