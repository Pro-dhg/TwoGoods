package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/10
 * @DESC
 */
@Data
@ApiModel
public class VisualizationHomeTopTenParse {

    @ApiModelProperty(value = "网站")
    private String websiteAppName;

    @ApiModelProperty(value = "分类")
    private String domainType;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "运营商")
    private String answerFirstIsp;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    private Integer rankNumber;
}
