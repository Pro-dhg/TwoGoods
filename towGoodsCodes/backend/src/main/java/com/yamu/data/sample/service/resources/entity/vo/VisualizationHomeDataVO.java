package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class VisualizationHomeDataVO {

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "时间粒度")
    private String queryType = "1min";

    @ApiModelProperty(value = "topn")
    private Long rankNumber;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "是否为今日")
    private Boolean isToday;

}
