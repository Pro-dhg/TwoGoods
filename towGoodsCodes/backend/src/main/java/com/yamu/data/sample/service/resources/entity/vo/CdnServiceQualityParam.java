package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualityParam {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时间粒度")
    private String queryType;

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "CDN厂商(输入框模糊查询)")
    private String businessLike;

    @ApiModelProperty(value = "CDN厂商")
    private String business;

    @ApiModelProperty(value = "资源属性")
    private String serviceProperties;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "省份名称(下钻)")
    private String answerFirstProvince;

    @ApiModelProperty(value = "本省(导出)")
    private String province;

    @ApiModelProperty(value = "本网(导出)")
    private String isp;

}
