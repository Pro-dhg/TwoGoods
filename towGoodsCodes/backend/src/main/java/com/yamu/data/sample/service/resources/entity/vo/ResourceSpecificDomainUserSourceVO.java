package com.yamu.data.sample.service.resources.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel
public class ResourceSpecificDomainUserSourceVO {

    @ApiModelProperty(value = "域名")
    private String domainName;

    @ApiModelProperty(value = "时间粒度")
    private String queryType = "1min";

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "ip类型")
    private String ipType;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "查询类型")
    private String qtype;

    @ApiModelProperty(value = "节点运营商code")
    private String ispCode;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "时间段时间")
    private String queryTime;

    @JsonIgnore
    private String queryTable = TABLE_PREFIX + "1min";

    @JsonIgnore
    private static final String TABLE_PREFIX = "rpt_resource_partner_domain_name_user_distribution_";

    public void setQueryType(String queryType) {

        this.queryType = queryType;
        if (StringUtils.isNotEmpty(queryType)) {
            this.queryTable = TABLE_PREFIX + queryType;
        }
    }

    @ApiModelProperty(value = "节点名")
    private String serverNodeName;
}
