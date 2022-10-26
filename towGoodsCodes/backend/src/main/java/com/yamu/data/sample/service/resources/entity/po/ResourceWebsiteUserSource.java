package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * Date 2022-03-14
 */
@Data
@ApiModel
public class ResourceWebsiteUserSource {

    @ApiModelProperty(value = "城市")
    private String answerFirstCity;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "网站名称")
    private String websiteAppName;
}
