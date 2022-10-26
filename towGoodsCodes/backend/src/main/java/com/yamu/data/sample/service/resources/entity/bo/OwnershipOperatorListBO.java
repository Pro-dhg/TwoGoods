package com.yamu.data.sample.service.resources.entity.bo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/03/15
 */

@Data
@ApiModel
public class OwnershipOperatorListBO {

    @ApiModelProperty(value = "时间段")
    private String time;

    @ApiModelProperty(value = "运营商")
    private String answerFirstIsp;

    @ApiModelProperty(value = "省份")
    private String answerFirstProvince;

    @ApiModelProperty(value = "城市")
    private String answerFirstCity;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "IPv4解析次数")
    private BigInteger v4Cnt;

    @ApiModelProperty(value = "IPv6解析次数")
    private BigInteger v6Cnt;

    @ApiModelProperty(value = "成功次数")
    private BigInteger parseSuccessCnt;

    @ApiModelProperty(value = "失败次数")
    private BigInteger parseFailCnt;
}
