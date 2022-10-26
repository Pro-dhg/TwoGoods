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
public class CdnServiceQualityCdnCoverList {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
}
