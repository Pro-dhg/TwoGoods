package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualityMapData {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排名")
    private Long rankNumber;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "节点数量")
    private BigInteger nodeCnt;

    @ApiModelProperty(value = "成功率")
    private Double successRate;

    @ApiModelProperty(value = "本网率")
    private Double netInRate;

}
