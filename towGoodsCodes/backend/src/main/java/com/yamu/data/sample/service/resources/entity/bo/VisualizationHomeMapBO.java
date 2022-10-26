package com.yamu.data.sample.service.resources.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
@ApiModel
public class VisualizationHomeMapBO {

    @ApiModelProperty(value = "排名")
    private Long rankNumber;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "省份名称")
    private String distribution;
}
