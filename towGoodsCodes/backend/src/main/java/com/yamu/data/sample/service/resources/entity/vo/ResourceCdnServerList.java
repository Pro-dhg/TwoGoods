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
public class ResourceCdnServerList {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "CDN厂商")
    private String business;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "网外次数")
    private BigInteger netOutParseTotalCnt;

    @ApiModelProperty(value = "资源节点个数")
    private BigInteger serverCnt;

    @ApiModelProperty(value = "网内资源节点个数")
    private BigInteger netInServerCnt;

    @ApiModelProperty(value = "网外资源节点个数")
    private BigInteger netOutServerCnt;

    @ApiModelProperty(value = "CDN域名")
    private String domainName;
}
