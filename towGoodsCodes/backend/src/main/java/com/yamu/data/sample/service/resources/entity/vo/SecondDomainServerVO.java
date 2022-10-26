package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @Author yuyuan.Dong
 * @Date 2022/3/17
 * @DESC
 */
@Data
public class SecondDomainServerVO {
    @ApiModelProperty(value = "服务IP")
    private String businessIp;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "省份")
    protected String province;

    @ApiModelProperty(value = "城市")
    protected String city;

    @ApiModelProperty(value = "运营商")
    protected String isp;
}
