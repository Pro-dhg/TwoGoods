package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zl.chen
 * @create 2022/10/21 14:55
 */
@Data
@ApiModel
public class CdnBusinessAssessRespVo {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "cdn厂商")
    private String business;

    @ApiModelProperty(value = "考核分数")
    private Double assessScore;

    @ApiModelProperty(value = "考核分数对比变化率")
    private Double assessScoreRateOfChange;

    @ApiModelProperty(value = "解析次数")
    private Long parseTotal;

    @ApiModelProperty(value = "解析次数对比变化率")
    private Double parseTotalRateOfChange;

    @ApiModelProperty(value = "成功率")
    private Double sucRate;

    @ApiModelProperty(value = "成功率对比变化率")
    private Double sucRateOfChange;

    @ApiModelProperty(value = "ICP调度准确率")
    private Double icpRate;

    @ApiModelProperty(value = "ICP调度准确率对比变化率")
    private Double icpRateOfChange;

    @ApiModelProperty(value = "本网率")
    private Double localNetRate;

    @ApiModelProperty(value = "本网率对比变化率")
    private Double localNetRateOfChange;

    @ApiModelProperty(value = "本省率")
    private Double localProRate;

    @ApiModelProperty(value = "本省率对比变化率")
    private Double localProRateOfChange;

    @ApiModelProperty(value = "邻省率")
    private Double neighborhoodRate;

    @ApiModelProperty(value = "邻省率对比变化率")
    private Double neighborhoodRateRateOfChange;

    @ApiModelProperty(value = "本市率")
    private Double localCityRate;

}
