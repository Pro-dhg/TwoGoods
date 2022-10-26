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
public class CdnServiceQualityDownLoad {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "厂商名称")
    private String business;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "占比")
    private String rate;

    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "覆盖网内城市数量")
    private BigInteger netInCityCnt;

    @ApiModelProperty(value = "覆盖网外城市数量")
    private BigInteger netOutCityCnt;

    @ApiModelProperty(value = "覆盖本省城市数量")
    private BigInteger withinCityCnt;

    @ApiModelProperty(value = "覆盖外省城市数量")
    private BigInteger withOutCityCnt;
}
