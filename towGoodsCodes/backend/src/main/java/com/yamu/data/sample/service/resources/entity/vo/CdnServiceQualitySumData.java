package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualitySumData {

    @ApiModelProperty(value = "厂商数量")
    private BigInteger businessCnt;

    @ApiModelProperty(value = "节点数量")
    private BigInteger nodeCnt;

    @ApiModelProperty(value = "网内无资源厂商数量")
    private BigInteger netInNoResourcesCnt;

    @ApiModelProperty(value = "省内无资源厂商数量")
    private BigInteger withinNoResourcesCnt;

    @ApiModelProperty(value = "本网率")
    private String netInRate;

    @ApiModelProperty(value = "本省率")
    private String withinRate;

    @ApiModelProperty(value = "用户请求到本市本网占比")
    private String userCityInNetInRate;

    @ApiModelProperty(value = "用户请求到本省本网占比")
    private String userWithinNetInRate;
}
