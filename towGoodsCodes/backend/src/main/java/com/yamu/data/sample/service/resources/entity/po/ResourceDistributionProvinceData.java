package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @author dys
 * Date 2022-03-14
 */
@Data
@ApiModel
public class ResourceDistributionProvinceData {

    @ApiModelProperty(value = "省份")
    private String answerFirstProvince;

    @ApiModelProperty(value = "明细数据")
    private List<ResourceDistributionProvinceDetail> data;

}
