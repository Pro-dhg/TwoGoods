package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * @Author yuyuan.Dong
 * @Date 2022/3/17
 * @DESC
 */
@Data
public class SecondDomainTableListVO {

    @ApiModelProperty(value = "时间")
    private Date parseTime;

    @ApiModelProperty(value = "时间段")
    private String timeRange;

    @ApiModelProperty(value = "公司名称")
    private String companyShortName;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;
}
