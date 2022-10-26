package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.common.entity.ParamPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dys
 * @date 2022/09/22
 */

@Data
@ApiModel
public class CdnTopNServiceParam extends ParamPage {

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "查询类型")
    private String qtype;

    @ApiModelProperty(value = "资源属性")
    private String resourceType;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "topN公司")
    private Long rankNumberCompany;

    @ApiModelProperty(value = "资源范围")
    private String resourceRange;

}
