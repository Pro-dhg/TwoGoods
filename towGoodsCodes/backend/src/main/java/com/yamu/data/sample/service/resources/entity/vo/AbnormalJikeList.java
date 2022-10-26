package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dys
 * @date 2022/09/22
 */

@Data
@ApiModel
public class AbnormalJikeList {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "ip")
    private String answerIp;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "IP地址所属省份")
    private String answerProvince;

    @ApiModelProperty(value = "IP地址所属城市")
    private String answerCity;

    @ApiModelProperty(value = "cdn厂商")
    private String cdnBusiness;

    @ApiModelProperty(value = "合作违规厂商")
    private String cdnIllegalBusiness;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "解析占比")
    private String parseTotalCntRate;

    @ApiModelProperty(value = "本网次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "本省次数")
    private BigInteger withinParseTotalCnt;
}
