package com.yamu.data.sample.service.resources.entity.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/05/13
 */

@Data
@ApiModel
public class NotRecordDomainNameListData {

    @ApiModelProperty(value = "条数")
    private Long total;

    @ApiModelProperty(value = "明细")
    private List<NotRecordDomainNameList> data;
}
