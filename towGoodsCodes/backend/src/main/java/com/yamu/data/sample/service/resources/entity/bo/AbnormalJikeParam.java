package com.yamu.data.sample.service.resources.entity.bo;

import com.yamu.data.sample.service.common.entity.ParamPageWithOutNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dys
 * @date 2022/09/22
 */

@Data
@ApiModel
public class AbnormalJikeParam extends ParamPageWithOutNode {

    @ApiModelProperty(value = "topN")
    private Long rankNumber;

    @ApiModelProperty(value = "cdn厂商")
    private List<String> cdnBusinessList;

    @ApiModelProperty(value = "合作违规厂商")
    private List<String> cdnIllegalBusinessList;

    @ApiModelProperty(value = "ip地址")
    private String answerIp;

    @ApiModelProperty(value = "域名")
    private String domainName;
}
