package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/03/15
 */

@Data
@ApiModel
public class ResourceOwnershipOperatorVO {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时间粒度")
    private String queryType;

    @ApiModelProperty(value = "分页码")
    private Long offset;

    @ApiModelProperty(value = "分页数量")
    private Long limit;

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "topN类型",example = "域名|网站|分类|公司")
    private String topNType;

    @ApiModelProperty(value = "V4/V6",example = "ipv4|ipv6")
    private String netType;

    @ApiModelProperty(value = "来源地区")
    private String userCity;

    @ApiModelProperty(value = "归属地")
    private List<String> provinceList;

    @ApiModelProperty(value = "运营商")
    private List<String> ispList;

    @ApiModelProperty(value = "本省")
    private String thisProvince;

    @ApiModelProperty(value = "统计方式",example = "分布属地:ownership|运营商:isp|分布属地&运营商:oAndIsp")
    private String statistics;

    @ApiModelProperty(value = "归属地境外")
    private Boolean abroadFlag;

}
