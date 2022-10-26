package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class RateTrend {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "本网率")
    private String netInRate;

    @ApiModelProperty(value = "本省率")
    private String withInRate;

    @ApiModelProperty(value = "本市率")
    private String cityInRate;
}
