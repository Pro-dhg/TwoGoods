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
public class RecordDomainNameList {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "域")
    private String domainName;

    @ApiModelProperty(value = "DNS查询次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "成功次数")
    private BigInteger parseSuccessCnt;

    @ApiModelProperty(value = "成功率")
    private String successRate;

    @ApiModelProperty(value = "出网次数")
    private BigInteger netOutParseTotalCnt;

    @ApiModelProperty(value = "出网率")
    private String netOutRate;

    @ApiModelProperty(value = "网内次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "本网率")
    private String netInRate;

    @ApiModelProperty(value = "错误次数")
    private BigInteger parseFailCnt;

    @ApiModelProperty(value = "本省次数")
    private BigInteger withinParseTotalCnt;

    @ApiModelProperty(value = "外省次数")
    private BigInteger withoutParseTotalCnt;

    @ApiModelProperty(value = "CDN")
    private BigInteger cdnParseTotalCnt;

    @ApiModelProperty(value = "IDC")
    private BigInteger idcParseTotalCnt;

    @ApiModelProperty(value = "请求占比")
    private String rate;

    @ApiModelProperty(value = "域名数")
    private BigInteger domainNameCnt;

    @ApiModelProperty(value = "备案")
    private String icpCode;

    @ApiModelProperty(value = "公司名")
    private String companyShortName;

    @ApiModelProperty(value = "所属网站")
    private String websiteName;

    @ApiModelProperty(value = "中国电信")
    private BigInteger zgdxCnt;

    @ApiModelProperty(value = "中国联通")
    private BigInteger zgltCnt;

    @ApiModelProperty(value = "港澳台")
    private BigInteger gatCnt;

    @ApiModelProperty(value = "境外")
    private BigInteger outCountryCnt;

    @ApiModelProperty(value = "未知")
    private BigInteger unknownCnt;
}
