package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dhg
 * @date 2022/10/20
 */

@Data
@ApiModel
public class PopularCnameDomainDowmloadList {

    @ApiModelProperty(value = "时间")
    private String parseTime;

    @ApiModelProperty(value = "CNAME")
    private String cname;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "目标IP")
    private String answerFirst;

    @ApiModelProperty(value = "IP所属省份")
    private String province;

    @ApiModelProperty(value = "IP所属运营商")
    private String isp;

    @ApiModelProperty(value = "解析次数")
    private String parseTotalCnt;

}
