package com.yamu.data.sample.service.resources.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/03/15
 */

@Data
@ApiModel
public class OwnershipOperatorDataBO {

    @ApiModelProperty(value = "条数")
    private Long total;

    @ApiModelProperty(value = "明细")
    private List<OwnershipOperatorListBO> data;

}
