package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * Date 2022-03-18
 */
@Data
@ApiModel
public class ResourceIpDetailData {

    @ApiModelProperty(value = "服务ip")
    private String answerFirstIp;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "运营商")
    private String answerFirstIsp;

}
