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
public class ResourceCdnServerDispatchTrend {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "资源节点个数")
    private BigInteger serverCnt;

    @ApiModelProperty(value = "网内资源节点个数")
    private BigInteger netInServerCnt;

    @ApiModelProperty(value = "网外资源节点个数")
    private BigInteger netOutServerCnt;
}
