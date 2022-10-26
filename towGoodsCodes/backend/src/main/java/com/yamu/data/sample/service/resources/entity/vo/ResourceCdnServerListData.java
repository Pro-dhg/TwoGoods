package com.yamu.data.sample.service.resources.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/06/24
 */

@Data
@ApiModel
public class ResourceCdnServerListData {

    @ApiModelProperty(value = "条数")
    private Long total;

    @ApiModelProperty(value = "明细列表")
    private List<ResourceCdnServerList> data;
}
