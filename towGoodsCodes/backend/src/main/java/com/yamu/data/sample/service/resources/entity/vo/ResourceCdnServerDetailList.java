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
public class ResourceCdnServerDetailList {

    @ApiModelProperty(value = "IP地址")
    private String answerFirst;

    @ApiModelProperty(value = "所属省份")
    private String answerFirstProvince;

    @ApiModelProperty(value = "所属运营商")
    private String answerFirstIsp;

    @ApiModelProperty(value = "所属节点")
    private String answerFirstServer;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
}
