package com.yamu.data.sample.service.resources.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
@ApiModel
public class VisualizationHomeKeyBusinessBO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "数据量")
    private BigInteger data;

    @ApiModelProperty(value = "数据率")
    private Double rate;

    @ApiModelProperty(value = "环比")
    private Double yoyRate;

    @ApiModelProperty(value = "同比")
    private Double momRate;

    @ApiModelProperty(value = "占比")
    private Double popRate;
}
