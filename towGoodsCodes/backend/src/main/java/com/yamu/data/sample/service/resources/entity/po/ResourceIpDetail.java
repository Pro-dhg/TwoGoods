package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * Date 2022-03-18
 */
@Data
@ApiModel
public class ResourceIpDetail {

    @ApiModelProperty(value = "条数")
    private Long total;

    @ApiModelProperty(value = "列表数据")
    private List<ResourceIpDetailData> data;

}
