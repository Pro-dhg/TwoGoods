package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dhg
 * @date 2022/10/19
 */

@Data
@ApiModel
public class PopularCnameDomainListData {

    @ApiModelProperty(value = "条数")
    private Long total;

    @ApiModelProperty(value = "明细")
    private List<PopularCnameDomainList> data;
}
