package com.yamu.data.sample.service.resources.entity.po;

import com.yamu.data.sample.service.common.entity.ParamPageWithOutNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dhg
 * @date 2022/10/19
 */

@Data
@ApiModel
public class PopularCnameDomainFlowTrendParam extends ParamPageWithOutNode {

    @ApiModelProperty(value = "域名合并")
    private String domainMerge;

    @ApiModelProperty(value = "域名类型")
    private String domainType;

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

}
