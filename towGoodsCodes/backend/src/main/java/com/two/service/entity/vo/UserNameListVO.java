package com.two.service.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dhg
 * @date 2022/10/19
 */

@Data
@ApiModel
public class UserNameListVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "解析时间")
    private String parseTimestamp;

    @ApiModelProperty(value = "行为")
    private String behavior;


}
