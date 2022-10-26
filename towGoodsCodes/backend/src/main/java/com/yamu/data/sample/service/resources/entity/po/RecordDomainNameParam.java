package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dys
 * @date 2022/04/01
 */

@Data
@ApiModel
public class RecordDomainNameParam {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时间粒度")
    private String queryType;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "分页码")
    private Long offset;

    @ApiModelProperty(value = "分页数量")
    private Long limit;

    @ApiModelProperty(value = "节点运营商code")
    private String ispCode;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "域名等级(全域名:domain、二级域:second)")
    private String domainGrade;

    @ApiModelProperty(value = "域")
    private String domainName;
}
