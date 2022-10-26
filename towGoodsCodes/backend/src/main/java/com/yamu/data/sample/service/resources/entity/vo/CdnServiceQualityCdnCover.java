package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author dys
 * @date 2022/07/26
 */

@Data
@ApiModel
public class CdnServiceQualityCdnCover {

    @ApiModelProperty(value = "CDN覆盖数量")
    private BigInteger cdnCoverCnt;

    @ApiModelProperty(value = "明细列表")
    private List<CdnServiceQualityCdnCoverList> data;
}
