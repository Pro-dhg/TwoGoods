package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/09/22
 */

@Data
@ApiModel
public class CdnBusinessTopNServiceCompanyList {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "cdn厂商")
    private String business;

    @ApiModelProperty(value = "服务域名所属公司")
    private String companyShortName;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "占比")
    private Double rate;

    @ApiModelProperty(value = "本网率")
    private Double netInRate;
}