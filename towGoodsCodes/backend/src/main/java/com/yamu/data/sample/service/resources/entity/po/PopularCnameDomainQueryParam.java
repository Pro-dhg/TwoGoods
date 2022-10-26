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
public class PopularCnameDomainQueryParam extends ParamPageWithOutNode {

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "别名")
    private String cname;

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "域名类型")
    private String domainType;

    @ApiModelProperty(value = "排序方式")
    private String sortMode;

    @ApiModelProperty(value = "域名合并")
    private String domainMerge;

    @ApiModelProperty(value = "三角符号->排序字段")
    private String sortKey;

    @ApiModelProperty(value = "三角使用->排序方式")
    private String sortOrder;

}
