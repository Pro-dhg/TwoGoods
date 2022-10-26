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
public class PopularCnameDomainNetInTrend {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "本网率")
    private String netInRate;

    @ApiModelProperty(value = "出网率")
    private String outNetRate;

    @ApiModelProperty(value = "本省率")
    private String withinParseRate;

    @ApiModelProperty(value = "出省率")
    private String withoutParseRate;

    @ApiModelProperty(value = "成功率")
    private String successRate;

}
