package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author dhg
 * @date 2022/10/19
 */

@Data
@ApiModel
public class PopularCnameDomainList {

    @ApiModelProperty(value = "时间段")
    private String parseTime;

    @ApiModelProperty(value = "别名")
    private String cname;

    @ApiModelProperty(value = "网站")
    private String websiteAppName;

    @ApiModelProperty(value = "公司")
    private String companyShortName;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "域名所属网站")
    private String domainWebsiteAppName;

    @ApiModelProperty(value = "域名所属公司")
    private String domainCompanyShortName;

    @ApiModelProperty(value = "出网流量")
    private BigInteger netOutParseFlowTotal;

    @ApiModelProperty(value = "出网次数")
    private BigInteger netOutParseTotalCnt;

    @ApiModelProperty(value = "解析次数")
    private BigInteger parseTotalCnt;

    @ApiModelProperty(value = "成功次数")
    private BigInteger parseSuccessCnt;

    @ApiModelProperty(value = "成功率")
    private String successRate;

    @ApiModelProperty(value = "总流量")
    private BigInteger parseFlowTotal;

    @ApiModelProperty(value = "出网流量占比")
    private String outNetFlowRate;

    @ApiModelProperty(value = "出网率")
    private String outNetRate;

    @ApiModelProperty(value = "网内流量")
    private BigInteger netInParseFlowTotal;

    @ApiModelProperty(value = "本网次数")
    private BigInteger netInParseTotalCnt;

    @ApiModelProperty(value = "本网率")
    private String netInRate;

    @ApiModelProperty(value = "本省次数")
    private BigInteger withinParseTotalCnt;

    @ApiModelProperty(value = "本省率")
    private String withinParseRate;

    @ApiModelProperty(value = "外省次数")
    private BigInteger withoutParseTotalCnt;

    @ApiModelProperty(value = "出省率")
    private String withoutParseRate;
}
