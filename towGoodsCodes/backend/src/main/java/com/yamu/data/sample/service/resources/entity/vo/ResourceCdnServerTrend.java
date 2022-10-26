package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/06/24
 */

@Data
@ApiModel
public class ResourceCdnServerTrend {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "往外次数")
    private BigInteger netOutParseTotalCnt;
}
