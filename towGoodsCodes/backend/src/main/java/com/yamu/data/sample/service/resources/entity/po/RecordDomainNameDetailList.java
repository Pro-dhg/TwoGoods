package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/05/13
 */

@Data
@ApiModel
public class RecordDomainNameDetailList {

    @ApiModelProperty(value = "域名")
    private String wholeDomainName;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
}
