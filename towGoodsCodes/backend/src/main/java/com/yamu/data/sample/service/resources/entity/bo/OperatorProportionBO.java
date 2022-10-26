package com.yamu.data.sample.service.resources.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/03/15
 */

@Data
@ApiModel
public class OperatorProportionBO {

    @ApiModelProperty(value = "运营商")
    private String answerFirstIsp;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
}
