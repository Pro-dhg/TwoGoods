package com.yamu.data.sample.service.resources.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigInteger;

/**
 * @Author dhg
 * @Date 2022/5/26
 * @DESC
 */
@Data
@ApiModel
public class ResourceCdnCacheDomainDetail {

    //时间
    @ApiModelProperty(value = "时间")
    protected String parseTime;

    //cdn域名
    @ApiModelProperty(value = "cdn域名")
    private String domainName;

    //服务网站
    @ApiModelProperty(value = "服务网站")
    private String websiteAppName;

    //cdn域名解析次数
    @ApiModelProperty(value = "解析次数")
    private BigInteger cdnDomainParseTotalCnt;

    //占比
    @ApiModelProperty(value = "占比")
    private Double cdnProportion;

}
