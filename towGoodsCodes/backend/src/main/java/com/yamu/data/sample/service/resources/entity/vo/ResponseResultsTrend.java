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
public class ResponseResultsTrend {

    @ApiModelProperty(value = "NOERROR")
    private BigInteger parseNoerrorCnt;

    @ApiModelProperty(value = "NXDOMAIN")
    private BigInteger parseNxdomainCnt;

    @ApiModelProperty(value = "SERVFAIL")
    private BigInteger parseServfailCnt;

    @ApiModelProperty(value = "REFUSED")
    private BigInteger parseRefusedCnt;

    @ApiModelProperty(value = "OTHER")
    private BigInteger parseOtherCnt;
}
