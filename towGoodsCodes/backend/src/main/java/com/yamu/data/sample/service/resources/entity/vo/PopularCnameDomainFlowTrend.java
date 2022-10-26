package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dhg
 * @date 2022/10/20
 */

@Data
@ApiModel
public class PopularCnameDomainFlowTrend {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "出网流量")
    private BigInteger netOutParseFlowTotal;

}
